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
    ThreadLocal<Map<String, Object>> local = new ThreadLocal<Map<String, Object>>()
    {
        @Override
        protected Map<String, Object> initialValue()
        {
            return new HashMap<String, Object>();
        }
    };

    public Pojo()
    {
        IronMaiden.announceLineNumber(31);
    }

    public String getName()
    {
        IronMaiden.announceLineNumber(54);
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void foo(Object[] array)
    {
        Pojo pojo = new Pojo();

        synchronized (LOGGER)
        {
            System.err.println("ENTER " + Thread.currentThread().getId());

            setName("test");

            int size = array.length;
            int hash = array.hashCode();
            Object t = array[0];
            array[1] = t;

            local.get().put("ALAN", new Event(new Line("p", 3))
            {
            });
        }
        array[0] = 1;

        pojo.name = "BAR";
        IronMaiden.putField(pojo, "name", "TEST");
    }
}
