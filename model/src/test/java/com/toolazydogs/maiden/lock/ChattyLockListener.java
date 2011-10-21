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

import java.util.logging.Logger;


/**
 * @version $Revision: $ $Date: $
 */
public class ChattyLockListener implements LockListener
{
    private final static String CLASS_NAME = ChattyLockListener.class.getName();
    private final static Logger LOGGER = Logger.getLogger(CLASS_NAME);

    public synchronized void waiting(Object object)
    {
        LOGGER.info(Thread.currentThread().getName() + ": WAITING");
        System.err.println(Thread.currentThread().getName() + ": WAITING");
        System.err.flush();
    }

    public synchronized void interrupted(Object object)
    {
        LOGGER.info(Thread.currentThread().getName() + ": INTERRUPTED");
        System.err.println(Thread.currentThread().getName() + ": INTERRUPTED");
        System.err.flush();
    }

    public synchronized void timeout(Object object)
    {
        LOGGER.info(Thread.currentThread().getName() + ": TIMEOUT");
        System.err.println(Thread.currentThread().getName() + ": TIMEOUT");
        System.err.flush();
    }

    public synchronized void obtained(Object object)
    {
        LOGGER.info(Thread.currentThread().getName() + ": OBTAINED");
        System.err.println(Thread.currentThread().getName() + ": OBTAINED");
        System.err.flush();
    }

    public synchronized void lock(Object object)
    {
        LOGGER.info(Thread.currentThread().getName() + ": LOCK");
        System.err.println(Thread.currentThread().getName() + ": LOCK");
        System.err.flush();
    }

    public synchronized void unlock(Object object)
    {
        LOGGER.info(Thread.currentThread().getName() + ": UNLOCK");
        System.err.println(Thread.currentThread().getName() + ": UNLOCK");
        System.err.flush();
    }
}
