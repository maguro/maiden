/**
 *
 * Copyright 2010-2011 (C) The original author or authors
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
package com.acme;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.toolazydogs.maiden.IronMaiden;
import com.toolazydogs.maiden.model.Event;
import com.toolazydogs.maiden.model.Line;


/**
 *
 */
public class Pojo
{
    private final static String CLASS_NAME = Pojo.class.getName();
    private final static Logger LOGGER = Logger.getLogger(CLASS_NAME);
    private volatile String name;
    private Object[] array = new Object[10];
    private ThreadLocal<Map<String, Object>> local = new ThreadLocal<Map<String, Object>>()
    {
        @Override
        protected Map<String, Object> initialValue()
        {
            return new HashMap<String, Object>();
        }
    };
    private static int hookCounter;
    private static final String intern;
    private volatile Integer vInteger;

    static
    {
        hookCounter = 4;
    }

    static
    {
        hookCounter = 7;
        intern = "IN".intern();
    }

    public Pojo()
    {
    }

    public synchronized native void setName(String name, int j) throws Exception;

    public native static void z_a(String name, int j) throws Exception;

    public synchronized static void a(String name, int j) throws Exception
    {
        z_a(name, j);
    }
    public void writeMessage( String message )
    {
    }

    public String getName()
    {
        return name;
    }

    public static synchronized void bar(Object[] array)
    {
        int i = 0;
        for (int j = i; j < 100000; i++)
        {
            Object t = array[0];
            array[1] = t;
        }
    }

    synchronized private static int getNextHook()
    {
//        IronMaiden.push("", "", "");
//        try
//        {
            try
            {
                bar(null);
                if (vint == 1) return -1;
            }
            catch (Exception e)
            {
                e.printStackTrace();  //Todo change body of catch statement use File | Settings | File Templates.
                return -2;
            }
            finally
            {
                vint = 4;
            }
            if (hookCounter == 1) return 17;
            return ++hookCounter;
//        }
//        finally
//        {
//            IronMaiden.pop(1);
//        }
    }

    public void foo(byte[] array) throws Exception
    {
        LOGGER.wait();
        vInteger = vint = 2;
        Pojo pojo = new Pojo();

        int i = vint;
        int j = vInteger;

        synchronized (LOGGER)
        {
            System.err.println("ENTER " + Thread.currentThread().getId());

            setName("test", 9);

            int size = array.length;
            int hash = array.hashCode();
            byte t = array[0];
            array[1] = t;

            local.get().put("ALAN", new Event(new Line("c.a.t", "p", "()V", 3))
            {
            });
        }
        array[0] = 1;

        pojo.name = "BAR";
        IronMaiden.putField(1, pojo, "name");
    }

    private static int vint;
}
