/**
 *
 * Copyright 2011 (C) The original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.toolazydogs.maiden;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.toolazydogs.maiden.api.IronListener;
import com.toolazydogs.maiden.model.MethodDesc;


/**
 *
 */
public class InMemoryDeadlockListener implements IronListener
{
    private final static String CLASS_NAME = InMemoryDeadlockListener.class.getName();
    private final static Logger LOGGER = Logger.getLogger(CLASS_NAME);
    private final ScheduledThreadPoolExecutor pool = new ScheduledThreadPoolExecutor(6, new ThreadFactory()
    {
        public Thread newThread(Runnable r)
        {
            Thread thread = new Thread(r);

            thread.setDaemon(true);
            thread.setName("Iron Maiden Deadlock Thread");

            return thread;
        }
    });
    private final ThreadLocal<Stack<MethodDesc>> callStack = new ThreadLocal<Stack<MethodDesc>>()
    {
        @Override
        protected Stack<MethodDesc> initialValue()
        {
            return new Stack<MethodDesc>();
        }
    };
    private final ReferenceQueue<Object> queue = new ReferenceQueue<Object>();
    private final Map<Wrapper, Lock> wrappers = new HashMap<Wrapper, Lock>();

    public InMemoryDeadlockListener()
    {
        pool.execute(new Runnable()
        {
            /**
             * Daemon thread to keep our collection of objects tidy
             */
            @SuppressWarnings({"unchecked"})
            public void run()
            {
                boolean done = false;
                while (!done)
                {
                    try
                    {
                        Reference<Object> reference = (Reference<Object>)queue.remove();
                        assert wrappers.remove(new Wrapper(reference)) != null;
                    }
                    catch (InterruptedException e)
                    {
                        done = true;
                    }
                }
            }
        });
    }

    public void push(String classname, String name, String desc)
    {
        LOGGER.entering(CLASS_NAME, "push", new Object[]{classname, name, desc});

        callStack.get().push(new MethodDesc(classname, name, desc));

        LOGGER.exiting(CLASS_NAME, "push");
    }

    public void pop(int line)
    {
        LOGGER.entering(CLASS_NAME, "pop", line);

        callStack.get().pop();

        LOGGER.exiting(CLASS_NAME, "pop");
    }

    @SuppressWarnings({"SynchronizationOnLocalVariableOrMethodParameter"})
    public void lockObject(int line, Object object)
    {
        LOGGER.entering(CLASS_NAME, "lockObject", new Object[]{line, object});

        try
        {
            Lock lock = fetchLock(object);

            synchronized (lock)
            {
                if (lock.locked == Thread.currentThread())
                {
                    lock.count++;
                    return;
                }

                if (lock.locked != null)
                {
                    if (LOGGER.isLoggable(Level.FINEST)) LOGGER.finest("LOCKED " + System.identityHashCode(object) + " by " + lock.locked);
                    lock.wait();
                }

                lock.locked = Thread.currentThread();
                lock.count++;
                if (LOGGER.isLoggable(Level.FINEST)) LOGGER.finest("LOCKING " + System.identityHashCode(object) + " by " + lock.locked);
            }
        }
        catch (InterruptedException e)
        {
            LOGGER.log(Level.WARNING, "Caught exception", e);
            Thread.currentThread().interrupt();
        }

        LOGGER.exiting(CLASS_NAME, "lockObject");
    }

    @SuppressWarnings({"SynchronizationOnLocalVariableOrMethodParameter"})
    public void unlockObject(int line, Object object)
    {
        LOGGER.entering(CLASS_NAME, "unlockObject", new Object[]{line, object});

        Lock lock = fetchLock(object);

        synchronized (lock)
        {
            assert lock.locked == Thread.currentThread();

            if (--lock.count == 0)
            {

                if (lock.getLockingQueue().isEmpty())
                {
                    if (LOGGER.isLoggable(Level.FINEST)) LOGGER.finest("UNLOCKING " + System.identityHashCode(object) + " by " + lock.locked);
                    lock.notify();
                    lock.locked = null;
                }
                else
                {
                    Waiting waiting = lock.getLockingQueue().poll();

                    if (LOGGER.isLoggable(Level.FINEST)) LOGGER.finest("UNLOCKING " + System.identityHashCode(object) + " from " + lock.locked + " to " + waiting.waiter);

                    lock.locked = waiting.waiter;
                    lock.count = waiting.count;

                    synchronized (waiting)
                    {
                        waiting.notify();
                    }
                }
            }
        }

        LOGGER.exiting(CLASS_NAME, "unlockObject");
    }

    public void readVolatile(int line, Object object, String field)
    {
    }

    public void writeVolatile(int line, Object object, String field)
    {
    }

    public void loadArray(int line, Object array, int index)
    {
    }

    public void storeArray(int line, Object array, int index)
    {
    }

    public void getField(int line, Object object, String name)
    {
    }

    public void putField(int line, Object object, String name)
    {
    }

    public void getStatic(int line, Class clazz, String name)
    {
    }

    public void putStatic(int line, Class clazz, String name)
    {
    }

    @SuppressWarnings({"SynchronizationOnLocalVariableOrMethodParameter"})
    public void waitStart(int line, Object object) throws InterruptedException
    {
        LOGGER.entering(CLASS_NAME, "waitStart", new Object[]{line, object});

        Lock lock = fetchLock(object);

        if (lock.locked != Thread.currentThread()) throw new IllegalMonitorStateException();

        Waiting waiting = new Waiting(Thread.currentThread(), lock.count);
        synchronized (lock)
        {
            Queue<Waiting> queue = lock.getWaitQueue();
            queue.add(waiting);

            lock.count = 0;
            lock.locked = null;
            lock.notify();
        }

        synchronized (waiting)
        {
            waiting.wait();
        }

        LOGGER.exiting(CLASS_NAME, "waitStart");
    }

    public void waitStart(int line, Object object, long milliseconds) throws InterruptedException
    {
        LOGGER.entering(CLASS_NAME, "waitStart", new Object[]{line, object, milliseconds});

        waitStart(line, object, milliseconds, 0);

        LOGGER.exiting(CLASS_NAME, "waitStart");
    }

    @SuppressWarnings({"SynchronizationOnLocalVariableOrMethodParameter"})
    public void waitStart(int line, Object object, long milliseconds, int nanoseconds) throws InterruptedException
    {
        LOGGER.entering(CLASS_NAME, "waitStart", new Object[]{line, object, milliseconds, nanoseconds});

        final Lock lock = fetchLock(object);

        if (lock.locked != Thread.currentThread()) throw new IllegalMonitorStateException();

        final Waiting waiting = new Waiting(Thread.currentThread(), lock.count);
        synchronized (lock)
        {
            Queue<Waiting> queue = lock.getWaitQueue();
            queue.add(waiting);

            lock.count = 0;
            lock.locked = null;
            lock.notify();
        }

        synchronized (waiting)
        {
            pool.schedule(new Runnable()
            {
                public void run()
                {
                    synchronized (lock)
                    {
                        Queue<Waiting> queue = lock.getWaitQueue();

                        if (queue.remove(waiting))
                        {
                            if (lock.locked == null)
                            {
                                lock.count = waiting.count;
                                lock.locked = waiting.waiter;

                                synchronized (waiting)
                                {
                                    waiting.notify();
                                }
                            }
                            else
                            {
                                lock.getLockingQueue().add(waiting);
                            }
                        }
                    }
                }
            }, 1000000 * milliseconds + nanoseconds, TimeUnit.NANOSECONDS);

            waiting.wait();
        }

        LOGGER.exiting(CLASS_NAME, "waitStart");
    }

    @SuppressWarnings({"SynchronizationOnLocalVariableOrMethodParameter"})
    public void notifyObject(int line, Object object)
    {
        LOGGER.entering(CLASS_NAME, "notifyObject", new Object[]{line, object});

        Lock lock = fetchLock(object);

        if (lock.locked != Thread.currentThread()) throw new IllegalMonitorStateException();

        synchronized (lock)
        {
            Queue<Waiting> queue = lock.getWaitQueue();

            Waiting waiting = queue.poll();
            if (waiting != null)
            {
                lock.count = waiting.count;
                lock.locked = waiting.waiter;

                synchronized (waiting)
                {
                    waiting.notify();
                }
            }
        }

        LOGGER.exiting(CLASS_NAME, "notifyObject");
    }

    @SuppressWarnings({"SynchronizationOnLocalVariableOrMethodParameter"})
    public void notifyAllObject(int line, Object object)
    {
        LOGGER.entering(CLASS_NAME, "notifyAllObject", new Object[]{line, object});

        Lock lock = fetchLock(object);

        if (lock.locked != Thread.currentThread()) throw new IllegalMonitorStateException();

        synchronized (lock)
        {
            Queue<Waiting> queue = lock.getWaitQueue();

            Waiting waiting = queue.poll();
            if (waiting != null)
            {
                lock.count = waiting.count;
                lock.locked = waiting.waiter;

                synchronized (waiting)
                {
                    waiting.notify();
                }
            }

            lock.getLockingQueue().addAll(queue);
            queue.clear();
        }

        LOGGER.exiting(CLASS_NAME, "notifyAllObject");
    }

    private synchronized Lock fetchLock(Object object)
    {
        Wrapper wrapper = new Wrapper(object);
        Lock lock = wrappers.get(wrapper);
        if (lock == null) wrappers.put(wrapper, lock = new Lock());
        return lock;
    }

    private class Wrapper
    {
        final Reference reference;

        private Wrapper(Object object)
        {
            assert object != null;
            this.reference = new WeakReference<Object>(object, queue);
        }

        private Wrapper(Reference reference)
        {
            assert reference != null;
            this.reference = reference;
        }

        @Override
        public boolean equals(Object o)
        {
            assert o instanceof Wrapper;

            Wrapper wrapper = (Wrapper)o;

            return System.identityHashCode(reference) == System.identityHashCode(wrapper.reference);
        }

        @Override
        public int hashCode()
        {
            return System.identityHashCode(reference);
        }
    }

    private static class Lock
    {
        Queue<Waiting> wait = null;
        Queue<Waiting> locking = null;
        Thread locked = null;
        int count = 0;

        Queue<Waiting> getWaitQueue()
        {
            if (wait == null) wait = new LinkedList<Waiting>();
            return wait;
        }

        Queue<Waiting> getLockingQueue()
        {
            if (locking == null) locking = new LinkedList<Waiting>();
            return locking;
        }
    }

    private static class Waiting
    {
        Thread waiter;
        int count;

        private Waiting(Thread waiter, int count)
        {
            assert waiter != null;
            assert count > 0;

            this.waiter = waiter;
            this.count = count;
        }
    }
}
