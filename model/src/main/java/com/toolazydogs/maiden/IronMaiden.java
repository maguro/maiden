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

import java.util.HashMap;
import java.util.Map;


/**
 *
 */
public class IronMaiden
{
    private final static Object LOCK = new Object();
    private final static ThreadLocal<String> CLASS_NAMES = new ThreadLocal<String>();
    private final static ThreadLocal<String> METHOD_NAMES = new ThreadLocal<String>();
    private final static ThreadLocal<String> METHOD_DESCS = new ThreadLocal<String>();
    private final static ThreadLocal<Integer> LINE_NUMBERS = new ThreadLocal<Integer>();
    private final static Map<Object, Map<String, Object>> FIELDS = new HashMap<Object, Map<String, Object>>();


    public static void announceMethod(String classname, String name, String desc)
    {
        CLASS_NAMES.set(classname);
        METHOD_NAMES.set(name);
        METHOD_DESCS.set(desc);
    }

    public static void announceLineNumber(int lineNumber)
    {
        LINE_NUMBERS.set(lineNumber);
    }

    public static void lockObject(Object object)
    {
    }

    public static void unlockObject(Object object)
    {
    }

    public static void putField(Object object, String name, Object value)
    {
        synchronized (LOCK)
        {
            Map<String, Object> fields = FIELDS.get(object);
            if (fields == null) FIELDS.put(object, fields = new HashMap<String, Object>());
            fields.put(name, value);
        }
    }

    public static Object getField(Object object, String name)
    {
        synchronized (LOCK)
        {
            Map<String, Object> fields = FIELDS.get(object);
            if (fields == null) return null;
            else return fields.get(name);
        }
    }
}
