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
package com.toolazydogs.maiden.util;

import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.Test;


/**
 *
 */
public class WeakIdentityHashMapTest
{
    @Test
    public void testSimpleFunctionality() throws Exception
    {
        Map<Integer, String> map = new WeakIdentityHashMap<Integer, String>();

        map.put(1, "REPLACEABLE");
        assertEquals(map.put(1, "FIRST"), "REPLACEABLE");
        System.gc();
        Thread.sleep(1000);
        assertEquals(map.size(), 1); // small primitives are cached instances of Integer

        Integer key = 570;
        map.put(key, "COLLECTIBLE");
        assertTrue(map.containsKey(key));
        assertFalse(map.containsKey(570));
        key = null;
        System.gc();
        Thread.sleep(1000);
        assertEquals(map.size(), 1); // small primitives are cached instances of Integer
    }
}
