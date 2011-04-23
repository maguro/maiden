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

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import com.toolazydogs.maiden.api.IronContext;
import com.toolazydogs.maiden.api.IronListener;


/**
 *
 */
public class IronMaiden
{
    private static Set<IronListener> LISTENERS = new CopyOnWriteArraySet<IronListener>();
    private static ThreadLocal<Boolean> INSIDE = new ThreadLocal<Boolean>()
    {
        @Override
        protected Boolean initialValue()
        {
            return Boolean.FALSE;
        }
    };

    /**
     * Allocate a context that can be used to set the listener
     *
     * @return an instance of an {@link IronContext}
     */
    public static IronContext allocateContext()
    {
        return new IronContext()
        {
            public boolean addListener(IronListener listener)
            {
                if (listener == null) throw new IllegalArgumentException("Listener cannot be null");

                return LISTENERS.add(listener);
            }

            public boolean removeListener(IronListener listener)
            {
                return listener != null && LISTENERS.remove(listener);

            }
        };
    }

    public static void push(String classname, String name, String desc)
    {
        if (INSIDE.get()) return;
        else INSIDE.set(true);

        try
        {
            for (IronListener listener : LISTENERS) listener.push(classname, name, desc);
        }
        finally
        {
            INSIDE.set(false);
        }
    }

    public static void pop(int line)
    {
        if (INSIDE.get()) return;
        else INSIDE.set(true);

        try
        {
            for (IronListener listener : LISTENERS) listener.pop(line);
        }
        finally
        {
            INSIDE.set(false);
        }
    }

    public static void lockObject(int line, Object object)
    {
        if (INSIDE.get()) return;
        else INSIDE.set(true);

        try
        {
            for (IronListener listener : LISTENERS) listener.lockObject(line, object);
        }
        finally
        {
            INSIDE.set(false);
        }
    }

    public static void unlockObject(int line, Object object)
    {
        if (INSIDE.get()) return;
        else INSIDE.set(true);

        try
        {
            for (IronListener listener : LISTENERS) listener.unlockObject(line, object);
        }
        finally
        {
            INSIDE.set(false);
        }
    }

    public static void readVolatile(int line, Object object, String field)
    {
        if (INSIDE.get()) return;
        else INSIDE.set(true);

        try
        {
            for (IronListener listener : LISTENERS) listener.readVolatile(line, object, field);
        }
        finally
        {
            INSIDE.set(false);
        }
    }

    public static void writeVolatile(int line, Object object, String field)
    {
        if (INSIDE.get()) return;
        else INSIDE.set(true);

        try
        {
            for (IronListener listener : LISTENERS) listener.writeVolatile(line, object, field);
        }
        finally
        {
            INSIDE.set(false);
        }
    }

    public static void loadArray(int line, Object array, int index)
    {
        if (INSIDE.get()) return;
        else INSIDE.set(true);

        try
        {
            for (IronListener listener : LISTENERS) listener.loadArray(line, array, index);
        }
        finally
        {
            INSIDE.set(false);
        }
    }

    public static void storeArray(int line, Object array, int index)
    {
        if (INSIDE.get()) return;
        else INSIDE.set(true);

        try
        {
            for (IronListener listener : LISTENERS) listener.storeArray(line, array, index);
        }
        finally
        {
            INSIDE.set(false);
        }
    }

    public static void getField(int line, Object object, String name)
    {
        if (INSIDE.get()) return;
        else INSIDE.set(true);

        try
        {
            for (IronListener listener : LISTENERS) listener.getField(line, object, name);
        }
        finally
        {
            INSIDE.set(false);
        }
    }

    public static void putField(int line, Object object, String name)
    {
        if (INSIDE.get()) return;
        else INSIDE.set(true);

        try
        {
            for (IronListener listener : LISTENERS) listener.putField(line, object, name);
        }
        finally
        {
            INSIDE.set(false);
        }
    }

    public static void putStatic(int line, Class clazz, String name)
    {
        if (INSIDE.get()) return;
        else INSIDE.set(true);

        try
        {
            for (IronListener listener : LISTENERS) listener.putStatic(line, clazz, name);
        }
        finally
        {
            INSIDE.set(false);
        }
    }

    public static void getStatic(int line, Class clazz, String name)
    {
        if (INSIDE.get()) return;
        else INSIDE.set(true);

        try
        {
            for (IronListener listener : LISTENERS) listener.getStatic(line, clazz, name);
        }
        finally
        {
            INSIDE.set(false);
        }
    }

    public static void waitStart(int line, Object object) throws InterruptedException
    {
        if (INSIDE.get()) return;
        else INSIDE.set(true);

        try
        {
            for (IronListener listener : LISTENERS) listener.waitStart(line, object);
        }
        finally
        {
            INSIDE.set(false);
        }
    }

    public static void waitStart(int line, Object object, long milliseconds) throws InterruptedException
    {
        if (INSIDE.get()) return;
        else INSIDE.set(true);

        try
        {
            for (IronListener listener : LISTENERS) listener.waitStart(line, object);
        }
        finally
        {
            INSIDE.set(false);
        }
    }

    public static void waitStart(int line, Object object, long milliseconds, int nanoseconds) throws InterruptedException
    {
        if (INSIDE.get()) return;
        else INSIDE.set(true);

        try
        {
            for (IronListener listener : LISTENERS) listener.waitStart(line, object);
        }
        finally
        {
            INSIDE.set(false);
        }
    }

    public static void notifyObject(int line, Object object)
    {
        if (INSIDE.get()) return;
        else INSIDE.set(true);

        try
        {
            for (IronListener listener : LISTENERS) listener.notifyObject(line, object);
        }
        finally
        {
            INSIDE.set(false);
        }
    }

    public static void notifyAllObject(int line, Object object)
    {
        if (INSIDE.get()) return;
        else INSIDE.set(true);

        try
        {
            for (IronListener listener : LISTENERS) listener.notifyAllObject(line, object);
        }
        finally
        {
            INSIDE.set(false);
        }
    }
}
