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

import org.mockito.InOrder;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;
import org.testng.annotations.Test;

import com.toolazydogs.maiden.api.IronContext;
import com.toolazydogs.maiden.api.IronListener;


/**
 *
 */
public class IronMaidenTest
{
    /**
     * There should always at least be a default listener in place.
     */
    @Test
    public void testDefault()
    {
        IronMaiden.push("CLASS", "METHOD", "DESC");
        IronMaiden.pop(1);
        IronMaiden.lockObject(2, this);
        IronMaiden.unlockObject(3, this);
        IronMaiden.readVolatile(4, this, "field");
        IronMaiden.writeVolatile(5, this, "field");
        IronMaiden.loadArray(6, new byte[1], 0);
        IronMaiden.storeArray(7, new byte[1], 0);
        IronMaiden.getField(8, this, "field");
        IronMaiden.putField(9, this, "field");
        IronMaiden.putStatic(10, this.getClass(), "field");
        IronMaiden.getStatic(11, this.getClass(), "field");
        IronMaiden.waitStart(12, this);
        IronMaiden.waitStop(13, this);
        IronMaiden.notifyFirstObject(14, this);
        IronMaiden.notifyAllObjects(15, this);
    }

    @Test
    public void testContext()
    {
        IronContext context = IronMaiden.allocateContext();

        assertNotNull(context);

        try
        {
            context.addListener(null);
            fail("Should not be able to set listener to null");
        }
        catch (IllegalArgumentException ignore)
        {
        }

        IronListener listener = mock(IronListener.class);
        assertTrue(context.addListener(listener));
    }

    @Test
    public void testListener()
    {
        IronListener listener = mock(IronListener.class);
        IronContext context = IronMaiden.allocateContext();
        assertFalse(context.removeListener(listener));
        assertTrue(context.addListener(listener));

        InOrder inOrder = inOrder(listener);

        IronMaiden.push("CLASS", "METHOD", "DESC");
        IronMaiden.pop(1);
        IronMaiden.lockObject(2, this);
        IronMaiden.unlockObject(3, this);
        IronMaiden.readVolatile(4, this, "field");
        IronMaiden.writeVolatile(5, this, "field");
        IronMaiden.loadArray(6, new byte[1], 0);
        IronMaiden.storeArray(7, new byte[1], 0);
        IronMaiden.getField(8, this, "field");
        IronMaiden.putField(9, this, "field");
        IronMaiden.putStatic(10, this.getClass(), "field");
        IronMaiden.getStatic(11, this.getClass(), "field");
        IronMaiden.waitStart(12, this);
        IronMaiden.waitStop(13, this);
        IronMaiden.notifyFirstObject(14, this);
        IronMaiden.notifyAllObjects(15, this);

        inOrder.verify(listener).push("CLASS", "METHOD", "DESC");
        inOrder.verify(listener).pop(1);
        inOrder.verify(listener).lockObject(2, this);
        inOrder.verify(listener).unlockObject(3, this);
        inOrder.verify(listener).readVolatile(4, this, "field");
        inOrder.verify(listener).writeVolatile(5, this, "field");
        inOrder.verify(listener).loadArray(6, new byte[1], 0);
        inOrder.verify(listener).storeArray(7, new byte[1], 0);
        inOrder.verify(listener).getField(8, this, "field");
        inOrder.verify(listener).putField(9, this, "field");
        inOrder.verify(listener).putStatic(10, this.getClass(), "field");
        inOrder.verify(listener).getStatic(11, this.getClass(), "field");
        inOrder.verify(listener).waitStart(12, this);
        inOrder.verify(listener).waitStop(13, this);
        inOrder.verify(listener).notifyFirstObject(14, this);
        inOrder.verify(listener).notifyAllObjects(15, this);

        assertTrue(context.removeListener(listener));
    }
}
