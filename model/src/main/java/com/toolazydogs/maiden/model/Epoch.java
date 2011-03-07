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
package com.toolazydogs.maiden.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;


/**
 *
 */
public class Epoch implements Map<String, Object>
{
    private final static String CLASS_NAME = Epoch.class.getName();
    private final static Logger LOGGER = Logger.getLogger(CLASS_NAME);
    private final Map<String, Object> map = new HashMap<String, Object>();
    private volatile Epoch parent;

    public int size()
    {
        int size = (parent == null ? 0 : parent.size());
        return size + map.size();
    }

    public boolean isEmpty()
    {
        boolean isEmpty = (parent == null || parent.isEmpty());
        return isEmpty || map.isEmpty();
    }

    public boolean containsKey(Object key)
    {
        boolean containsKey = (parent == null || parent.containsKey(key));
        return containsKey || map.containsKey(key);
    }

    public boolean containsValue(Object value)
    {
        boolean containsValue = (parent == null || parent.containsValue(value));
        return containsValue || map.containsValue(value);
    }

    public Object get(Object key)
    {
        return null;  //Todo change body of implemented methods use File | Settings | File Templates.
    }

    public Object put(String key, Object value)
    {
        return map.put(key, value);
    }

    public Object remove(Object key)
    {
        return null;  //Todo change body of implemented methods use File | Settings | File Templates.
    }

    public void putAll(Map<? extends String, ? extends Object> m)
    {
        //Todo change body of implemented methods use File | Settings | File Templates.
    }

    public void clear()
    {
        //Todo change body of implemented methods use File | Settings | File Templates.
    }

    public Set<String> keySet()
    {
        return null;  //Todo change body of implemented methods use File | Settings | File Templates.
    }

    public Collection<Object> values()
    {
        return null;  //Todo change body of implemented methods use File | Settings | File Templates.
    }

    public Set<Entry<String, Object>> entrySet()
    {
        return null;  //Todo change body of implemented methods use File | Settings | File Templates.
    }
}
