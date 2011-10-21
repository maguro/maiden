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
package com.toolazydogs.maiden.lock;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.toolazydogs.maiden.api.DoNothingIronListener;
import com.toolazydogs.maiden.util.WeakIdentityHashMap;


/**
 *
 */
public class InMemoryDeadlockListener extends DoNothingIronListener
{
    private final static String CLASS_NAME = InMemoryDeadlockListener.class.getName();
    private final static Logger LOGGER = Logger.getLogger(CLASS_NAME);
    private final static int NOT_USED = -1;
    private final ScheduledExecutorService pool = Executors.newSingleThreadScheduledExecutor(new ThreadFactory()
    {
        public Thread newThread(Runnable r)
        {
            Thread thread = new Thread(r);

            thread.setDaemon(true);
            thread.setName("Iron Maiden Deadlock Thread");

            return thread;
        }
    });
    private final WeakIdentityHashMap<Object, Lock> locks = new WeakIdentityHashMap<Object, Lock>();
    private final Set<LockListener> listeners = new CopyOnWriteArraySet<LockListener>();

    public InMemoryDeadlockListener()
    {
        pool.scheduleWithFixedDelay(new Runnable()
        {
            /**
             * Daemon thread to keep our collection of objects tidy
             */
            @SuppressWarnings({"unchecked"})
            public void run()
            {
                assert Thread.currentThread().isDaemon();

                synchronized (locks)
                {
                    locks.purge();
                }
            }
        }, 10, 10, TimeUnit.MILLISECONDS);
    }

    public Set<LockListener> getListeners()
    {
        return listeners;
    }

    @SuppressWarnings({"SynchronizationOnLocalVariableOrMethodParameter"})
    public void lockObject(int line, Object object)
    {
        LOGGER.entering(CLASS_NAME, "lockObject", new Object[]{line, object});

        Lock lock = fetchLock(object);

        synchronized (lock)
        {
            if (lock.locked == Thread.currentThread())
            {
                assert lock.count > 0;

                lock.count++;
                if (LOGGER.isLoggable(Level.FINEST)) LOGGER.finest("INCREMENTED lock count on " + System.identityHashCode(object) + " to " + lock.count);
            }
            else if (lock.locked != null)
            {
                if (LOGGER.isLoggable(Level.FINEST)) LOGGER.finest("WAITING " + System.identityHashCode(object) + " by " + lock.locked);

                Waiting waiting = new Waiting();
                Queue<Waiting> locking = lock.getLockingQueue();
                locking.add(waiting);

                broadcastWait(object);

                boolean done = false;
                while (!done)
                {
                    try
                    {
                        lock.wait();
                        if (lock.nextLocker == waiting)
                        {
                            done = true;
                            lock.nextLocker = null;
                        }
                    }
                    catch (InterruptedException ie)
                    {
                        /**
                         * We're simulating a real lock and so we should not
                         * jump out of this loop.  Simply reset the interrupt
                         * and continue to wait.
                         */
                        Thread.currentThread().interrupt();
                    }
                }

                broadcastObtained(object);

                lock.locked = Thread.currentThread();
                lock.count = 1;

                if (LOGGER.isLoggable(Level.FINEST)) LOGGER.finest("OBTAINED " + System.identityHashCode(object) + " by " + lock.locked);
            }
            else
            {
                broadcastLock(object);

                lock.locked = Thread.currentThread();
                lock.count = 1;

                if (LOGGER.isLoggable(Level.FINEST)) LOGGER.finest("LOCKED " + System.identityHashCode(object) + " by " + lock.locked);
            }
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
            assert lock.count > 0;

            if (LOGGER.isLoggable(Level.FINEST)) LOGGER.finest("DECREMENTED lock count on " + System.identityHashCode(object) + " to " + (lock.count - 1));

            if (--lock.count == 0)
            {
                if (LOGGER.isLoggable(Level.FINEST)) LOGGER.finest("UNLOCKED " + System.identityHashCode(object) + " by " + lock.locked);

                lock.locked = null;

                broadcastUnlock(object);

                Queue<Waiting> locking = lock.getLockingQueue();

                assert lock.nextLocker == null;
                lock.nextLocker = locking.poll();
                if (LOGGER.isLoggable(Level.FINEST)) LOGGER.finest("Notifying all lock waiters on " + System.identityHashCode(object));

                lock.notifyAll();
            }
        }

        LOGGER.exiting(CLASS_NAME, "unlockObject");
    }

    @SuppressWarnings({"SynchronizationOnLocalVariableOrMethodParameter"})
    public void waitStart(int line, Object object) throws InterruptedException
    {
        LOGGER.entering(CLASS_NAME, "waitStart", new Object[]{line, object});

        Lock lock = fetchLock(object);

        if (lock.locked != Thread.currentThread()) throw new IllegalMonitorStateException();

        Waiting waiting = new Waiting(lock.count);
        synchronized (lock)
        {
            Queue<Waiting> waitQueue = lock.getWaitingQueue();
            Queue<Waiting> lockQueue = lock.getLockingQueue();
            waitQueue.add(waiting);

            broadcastUnlock(object);

            lock.count = 0;
            lock.locked = null;

            if (lock.nextLocker == null) lock.nextLocker = lockQueue.poll();

            boolean interrupted = false;
            boolean done = false;
            while (!done)
            {
                lock.notifyAll();
                try
                {
                    lock.wait();
                    if (!waitQueue.contains(waiting))
                    {
                        done = true;
                    }
                }
                catch (InterruptedException ie)
                {
                    /**
                     * If we're interrupted then we need to remove the thread 
                     * from the waiting queue.
                     */
                    interrupted = true;
                    waitQueue.remove(waiting);
                    lockQueue.add(waiting);
                }
            }

            broadcastWait(object);

            done = false;
            while (!done)
            {
                if (lock.nextLocker == waiting)
                {
                    done = true;
                    lock.nextLocker = null;

                    /**
                     * We are resuming a lock and so we should restore its count.
                     */
                    lock.locked = Thread.currentThread();
                    lock.count = waiting.count;
                }
                else
                {
                    try
                    {
                        lock.wait();
                    }
                    catch (InterruptedException ignored)
                    {
                        /**
                         * We're waiting for the lock, not waiting for a 
                         * notification.  Simply reset the interrupt
                         * and continue to wait.
                         */
                        if (!interrupted) Thread.currentThread().interrupt();
                    }
                }
            }

            broadcastObtained(object);

            if (interrupted) throw new InterruptedException();
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
    public void waitStart(int line, final Object object, long milliseconds, int nanoseconds) throws InterruptedException
    {
        LOGGER.entering(CLASS_NAME, "waitStart", new Object[]{line, object, milliseconds, nanoseconds});

        final Lock lock = fetchLock(object);

        if (lock.locked != Thread.currentThread()) throw new IllegalMonitorStateException();

        final Waiting waiting = new Waiting(lock.count);
        synchronized (lock)
        {
            final Thread me = Thread.currentThread();
            final AtomicBoolean waitBroken = new AtomicBoolean(false);
            final Queue<Waiting> waitQueue = lock.getWaitingQueue();
            final Queue<Waiting> lockQueue = lock.getLockingQueue();
            waitQueue.add(waiting);

            broadcastUnlock(object);

            lock.count = 0;
            lock.locked = null;

            Future handle = pool.schedule(new Runnable()
            {
                public void run()
                {
                    synchronized (lock)
                    {
                        waitBroken.set(true);
                        me.interrupt();

                        if (LOGGER.isLoggable(Level.FINEST)) LOGGER.finest("Wait lock broken on " + System.identityHashCode(object));
                    }
                }
            }, 1000000 * milliseconds + nanoseconds, TimeUnit.NANOSECONDS);

            if (LOGGER.isLoggable(Level.FINEST)) LOGGER.finest("Entering wait on " + System.identityHashCode(object));

            boolean done = false;
            while (!done)
            {
                assert waitBroken.get() || waitQueue.contains(waiting);

                try
                {
                    lock.wait();
                    if (waitQueue.peek() == waiting)
                    {
                        done = true;
                        lockQueue.add(waiting);
                    }
                }
                catch (InterruptedException ie)
                {
                    /**
                     * If we're interrupted then we need to remove the thread 
                     * from the waiting queue.
                     */
                    waitQueue.remove(waiting);
                    if (waitBroken.get())
                    {
                        done = true;
                        lockQueue.add(waiting);
                    }
                    else
                    {
                        throw ie;
                    }
                }
            }

            handle.cancel(false);

            if (LOGGER.isLoggable(Level.FINEST))
            {
                LOGGER.finest("Wait completed on " + System.identityHashCode(object));
                LOGGER.finest("Waiting for re-lock on " + System.identityHashCode(object));
            }

            done = false;
            while (!done)
            {
                assert lockQueue.contains(waiting);

                if (lockQueue.peek() == waiting)
                {
                    done = true;

                    /**
                     * We are resuming a lock and so we should restore its count.
                     */
                    lock.locked = Thread.currentThread();
                    lock.count = lockQueue.poll().count;
                }
                else
                {
                    try
                    {
                        lock.wait();
                    }
                    catch (InterruptedException ie)
                    {
                        /**
                         * If we're interrupted then we need to remove the thread 
                         * from the locking queue.
                         */
                        lockQueue.remove(waiting);
                        throw ie;
                    }
                }
            }

            if (LOGGER.isLoggable(Level.FINEST)) LOGGER.finest("RESTORED " + System.identityHashCode(object) + " by " + lock.locked);

            broadcastObtained(object);
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
            Queue<Waiting> waitQueue = lock.getWaitingQueue();

            Waiting waiting = waitQueue.poll();
            if (waiting != null)
            {
                lock.getLockingQueue().add(waiting);
                lock.notifyAll();
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
            Queue<Waiting> waitQueue = lock.getWaitingQueue();
            Queue<Waiting> lockQueue = lock.getLockingQueue();

            lockQueue.addAll(waitQueue);
            waitQueue.clear();

            lock.notifyAll();
        }

        LOGGER.exiting(CLASS_NAME, "notifyAllObject");
    }

    private synchronized Lock fetchLock(Object object)
    {
        synchronized (locks)
        {
            Lock lock = locks.get(object);
            if (lock == null) locks.put(object, lock = new Lock());
            return lock;
        }
    }

    private void broadcastWait(Object object)
    {
        for (LockListener listener : listeners) listener.waiting(object);
    }

    private void broadcastObtained(Object object)
    {
        for (LockListener listener : listeners) listener.obtained(object);
    }

    private void broadcastLock(Object object)
    {
        for (LockListener listener : listeners) listener.lock(object);
    }

    private void broadcastUnlock(Object object)
    {
        for (LockListener listener : listeners) listener.unlock(object);
    }

    private static class Lock
    {
        // Queue of threads waiting to be notified
        Queue<Waiting> waiting = null;
        Waiting nextWaiter = null;
        // Queue of threads waiting for access to a lock
        Queue<Waiting> locking = null;
        Waiting nextLocker = null;
        Thread locked = null;
        int count = 0;

        Queue<Waiting> getWaitingQueue()
        {
            if (waiting == null) waiting = new LinkedList<Waiting>();
            return waiting;
        }

        Queue<Waiting> getLockingQueue()
        {
            if (locking == null) locking = new LinkedList<Waiting>();
            return locking;
        }
    }

    private static class Waiting
    {
        // saved lock count
        final int count;

        private Waiting()
        {
            this(NOT_USED);
        }

        private Waiting(int count)
        {
            this.count = count;
        }
    }
}
