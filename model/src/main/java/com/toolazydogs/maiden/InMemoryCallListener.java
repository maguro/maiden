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
import com.toolazydogs.maiden.model.Invocation;
import com.toolazydogs.maiden.model.MethodDesc;
import com.toolazydogs.maiden.model.StartMethodDesc;


/**
 *
 */
public class InMemoryCallListener implements IronListener
{
    private final static String CLASS_NAME = InMemoryCallListener.class.getName();
    private final static Logger LOGGER = Logger.getLogger(CLASS_NAME);
    private final ThreadLocal<Stack<Invocation>> callStack = new ThreadLocal<Stack<Invocation>>()
    {
        @Override
        protected Stack<Invocation> initialValue()
        {
            return new Stack<Invocation>();
        }
    };
    private final ThreadLocal<Stack<MethodDesc>> methodStack = new ThreadLocal<Stack<MethodDesc>>()
    {
        @Override
        protected Stack<MethodDesc> initialValue()
        {
            Stack<MethodDesc> stack = new Stack<MethodDesc>();
            stack.push(new StartMethodDesc());
            return stack;
        }
    };

    public ThreadLocal<Stack<Invocation>> getCallStack()
    {
        return callStack;
    }

    public void call(int line, String classname, String name, String desc)
    {
        LOGGER.entering(CLASS_NAME, "call", new Object[]{line, classname, name, desc});

//        updateLine(line);

        LOGGER.exiting(CLASS_NAME, "call");
    }

    public void push(String classname, String name, String desc)
    {
        LOGGER.entering(CLASS_NAME, "push", new Object[]{classname, name, desc});

        MethodDesc methodDesc = new MethodDesc(classname, name, desc);
//        methodStack.get().push(methodDesc);
        callStack.get().push(new Invocation(methodDesc, -1));

        LOGGER.exiting(CLASS_NAME, "push");
    }

    public void pop(int line)
    {
        LOGGER.entering(CLASS_NAME, "pop", line);

        callStack.get().pop();
//        methodStack.get().pop();

        LOGGER.exiting(CLASS_NAME, "pop");
    }

    public void lockObject(int line, Object object) { updateLine(line); }

    public void unlockObject(int line, Object object) { updateLine(line); }

    public void readVolatile(int line, Object object, String field) { updateLine(line); }

    public void writeVolatile(int line, Object object, String field) { updateLine(line); }

    public void loadArray(int line, Object array, int index) { updateLine(line); }

    public void storeArray(int line, Object array, int index) { updateLine(line); }

    public void getField(int line, Object object, String name) { updateLine(line); }

    public void putField(int line, Object object, String name) { updateLine(line); }

    public void getStatic(int line, Class clazz, String name) { updateLine(line); }

    public void putStatic(int line, Class clazz, String name) { updateLine(line); }

    public void waitStart(int line, Object object) throws InterruptedException { updateLine(line); }

    public void waitStart(int line, Object object, long milliseconds) throws InterruptedException { updateLine(line); }

    public void waitStart(int line, Object object, long milliseconds, int nanoseconds) throws InterruptedException { updateLine(line); }

    public void notifyObject(int line, Object object) { updateLine(line); }

    public void notifyAllObject(int line, Object object) { updateLine(line); }

    private void updateLine(int line)
    {
        MethodDesc methodDesc = methodStack.get().peek();
        callStack.get().pop();
        callStack.get().push(new Invocation(methodDesc, line));
    }
}
