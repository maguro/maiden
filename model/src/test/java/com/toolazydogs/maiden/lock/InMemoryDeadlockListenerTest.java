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

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertSame;
import org.testng.annotations.Test;


/**
 *
 */
public class InMemoryDeadlockListenerTest
{
    @Test
    public void testWrapper()
    {
        Map<InMemoryDeadlockListener.Wrapper, Object> wrappers = new HashMap<InMemoryDeadlockListener.Wrapper, Object>();
        ReferenceQueue<Object> referenceQueue = new ReferenceQueue<Object>();
        Object object = "TEST";

        InMemoryDeadlockListener.Wrapper wrapper = new InMemoryDeadlockListener.Wrapper(new WeakReference<Object>(object, referenceQueue));
        wrappers.put(wrapper, object);

        assertSame(wrappers.get(wrapper), object);
    }
}
