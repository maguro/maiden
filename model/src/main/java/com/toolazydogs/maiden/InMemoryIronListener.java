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

import java.util.Stack;
import java.util.logging.Logger;

import com.toolazydogs.maiden.api.IronListener;
import com.toolazydogs.maiden.model.Line;
import com.toolazydogs.maiden.model.MethodDesc;
import com.toolazydogs.maiden.model.Region;


/**
 *
 */
public class InMemoryIronListener implements IronListener
{
    private final static String CLASS_NAME = InMemoryIronListener.class.getName();
    private final static Logger LOGGER = Logger.getLogger(CLASS_NAME);
    private final ThreadLocal<Line> LINE = new ThreadLocal<Line>();
    private final ThreadLocal<Region> PARENT = new ThreadLocal<Region>();
    private final ThreadLocal<Region> CURRENT = new ThreadLocal<Region>();
    private final ThreadLocal<Stack<Object>> STACK = new ThreadLocal<Stack<Object>>()
    {
        @Override
        protected Stack<Object> initialValue()
        {
            return new Stack<Object>();
        }
    };
    private final ThreadLocal<Stack<MethodDesc>> CALL_STACK = new ThreadLocal<Stack<MethodDesc>>()
    {
        @Override
        protected Stack<MethodDesc> initialValue()
        {
            return new Stack<MethodDesc>();
        }
    };

    public void push(String classname, String name, String desc)
    {
//        indent();
//        System.err.println("PUSH: " + classname + "." + name + " " + desc);
        CALL_STACK.get().push(new MethodDesc(classname, name, desc));
    }

    public void pop(int line)
    {
        if (CALL_STACK.get().isEmpty())
        {
            System.err.print(System.identityHashCode(Thread.currentThread()));
            System.err.println("***** OI ");
            Thread[] threads = new Thread[Thread.activeCount()];
            Thread.enumerate(threads);
            for (Thread t : threads)
            {
                System.err.println(t.getId() + " " + t.getName());
            }
            System.exit(0);
        }
        if (line == -1)
        {
//            System.err.print(System.identityHashCode(Thread.currentThread()));
//            System.err.println("***** THROW ");
        }
        MethodDesc methodDesc = CALL_STACK.get().pop();
//        indent();
//        System.err.println("POP:  " + methodDesc.getClassname() + "." + methodDesc.getName() + " " + methodDesc.getDesc() + "#" + line);
    }

    public void indent()
    {
        System.err.print(System.identityHashCode(Thread.currentThread()));
        int size = CALL_STACK.get().size();
        for (int i = 0; i < size; i++) System.err.print(" ");
    }

    public void lockObject(int line, Object object)
    {
        LOGGER.entering(CLASS_NAME, "lockObject", object);

        STACK.get().push(object);
        CURRENT.set(new Region(CURRENT.get(), LINE.get()));

        LOGGER.exiting(CLASS_NAME, "lockObject", object);
    }

    public void unlockObject(int line, Object object)
    {
        LOGGER.entering(CLASS_NAME, "lockObject", object);

        Object test = STACK.get().pop();
        if (test != object)
        {
            System.err.println("NOT EQUALS *************");
            System.err.println("   F:  " + System.identityHashCode(test) + " " + test.getClass() + " " + test);
            System.err.println("   W:  " + System.identityHashCode(object) + " " + object.getClass() + " " + object);
            System.err.println("   S:  " + STACK.get().size());
        }

        LOGGER.exiting(CLASS_NAME, "lockObject", object);
    }

    public void readVolatile(int line, Object object, String field)
    {
        //Todo change body of implemented methods use File | Settings | File Templates.
    }

    public void writeVolatile(int line, Object object, String field)
    {
        //Todo change body of implemented methods use File | Settings | File Templates.
    }

    public void loadArray(int line, Object array, int index)
    {
        //Todo change body of implemented methods use File | Settings | File Templates.
    }

    public void storeArray(int line, Object array, int index)
    {
        //Todo change body of implemented methods use File | Settings | File Templates.
    }

    public void getField(int line, Object object, String name)
    {
        //Todo change body of implemented methods use File | Settings | File Templates.
    }

    public void putField(int line, Object object, String name)
    {
        //Todo change body of implemented methods use File | Settings | File Templates.
    }

    public void getStatic(int line, Class clazz, String name)
    {
        //Todo change body of implemented methods use File | Settings | File Templates.
    }

    public void putStatic(int line, Class clazz, String name)
    {
        //Todo change body of implemented methods use File | Settings | File Templates.
    }

    public void waitStart(int line, Object object)
    {
        //Todo change body of implemented methods use File | Settings | File Templates.
    }

    public void waitStop(int line, Object object)
    {
        //Todo change body of implemented methods use File | Settings | File Templates.
    }

    public void notifyFirstObject(int line, Object object)
    {
        //Todo change body of implemented methods use File | Settings | File Templates.
    }

    public void notifyAllObjects(int line, Object object)
    {
        //Todo change body of implemented methods use File | Settings | File Templates.
    }
}
