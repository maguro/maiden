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

import java.util.concurrent.CountDownLatch;

import org.mockito.InOrder;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import org.mockito.exceptions.verification.VerificationInOrderFailure;
import static org.testng.FileAssert.fail;
import org.testng.annotations.Test;


/**
 *
 */
public class InMemoryDeadlockListenerTest
{
    @Test
    public void testEmpty()
    {
        InMemoryDeadlockListener deadlockListener = new InMemoryDeadlockListener();
        LockListener mock = mock(LockListener.class);
        Object a = new Object();
        Object b = new Object();

        deadlockListener.getListeners().add(mock);

        deadlockListener.push("com.acme.Pojo", "method", "()");
        deadlockListener.lockObject(3, a);
        deadlockListener.lockObject(4, b);
        deadlockListener.unlockObject(5, b);
        deadlockListener.unlockObject(6, a);
        deadlockListener.pop(15);

        verify(mock).lock(a);
        verify(mock).lock(b);
        verify(mock).unlock(b);
        verify(mock).unlock(a);
    }

    @Test
    public void testCount()
    {
        InMemoryDeadlockListener deadlockListener = new InMemoryDeadlockListener();
        LockListener mock = mock(LockListener.class);
        deadlockListener.getListeners().add(mock);

        Object a = new Object();

        deadlockListener.lockObject(3, a);
        deadlockListener.lockObject(4, a);
        deadlockListener.unlockObject(5, a);

        verify(mock).lock(a);
        verify(mock, never()).unlock(a);
    }

    @Test
    public void testWaitNotify() throws Exception
    {
        final InMemoryDeadlockListener deadlockListener = new InMemoryDeadlockListener();
        LockListener mock = mock(LockListener.class);
        deadlockListener.getListeners().add(mock);
        deadlockListener.getListeners().add(new ChattyLockListener());

        final Object a = new Object();

        final CountDownLatch first = new CountDownLatch(1);
        final CountDownLatch done = new CountDownLatch(2);
        new Thread(new Runnable()
        {
            public void run()
            {
                try
                {
                    first.await();

                    deadlockListener.lockObject(1, a);
                    deadlockListener.notifyAllObject(2, a);
                    deadlockListener.unlockObject(3, a);
                }
                catch (InterruptedException ignored)
                {
                    fail();
                }
                finally
                {
                    done.countDown();
                }
            }
        }, "ONE").start();

        new Thread(new Runnable()
        {
            public void run()
            {
                try
                {
                    deadlockListener.lockObject(1, a);
                    first.countDown();
                    deadlockListener.waitStart(2, a);
                    deadlockListener.unlockObject(3, a);
                }
                catch (InterruptedException ignored)
                {
                    fail();
                }
                finally
                {
                    done.countDown();
                }
            }
        }, "TWO").start();

        done.await();

        try
        {
            InOrder order = inOrder(mock);

            order.verify(mock).lock(a);
            order.verify(mock).waiting(a);
            order.verify(mock).unlock(a);
            order.verify(mock).obtained(a);
            order.verify(mock).waiting(a);
            order.verify(mock).unlock(a);
            order.verify(mock).obtained(a);
            order.verify(mock).unlock(a);

            order.verifyNoMoreInteractions();
        }
        catch (VerificationInOrderFailure ciof)
        {
            try
            {
                InOrder order = inOrder(mock);

                order.verify(mock).lock(a);
                order.verify(mock).unlock(a);
                order.verify(mock).lock(a);
                order.verify(mock).unlock(a);
                order.verify(mock).waiting(a);
                order.verify(mock).obtained(a);
                order.verify(mock).unlock(a);

                order.verifyNoMoreInteractions();
            }
            catch (VerificationInOrderFailure e)
            {
                InOrder order = inOrder(mock);

                order.verify(mock).lock(a);
                order.verify(mock).waiting(a);
                order.verify(mock).unlock(a);
                order.verify(mock).obtained(a);
                order.verify(mock).unlock(a);
                order.verify(mock).waiting(a);
                order.verify(mock).obtained(a);
                order.verify(mock).unlock(a);

                order.verifyNoMoreInteractions();
            }
        }
    }
}
