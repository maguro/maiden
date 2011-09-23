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
package com.toolazydogs.maiden.api;

import java.util.logging.Logger;


/**
 * A handy default implementation of {@link IronListener} whose methods do
 * nothing.
 */
public class DoNothingIronListener implements IronListener
{
    private final static String CLASS_NAME = DoNothingIronListener.class.getName();
    private final static Logger LOGGER = Logger.getLogger(CLASS_NAME);

    public void call(int line, String classname, String name, String desc)
    {
        LOGGER.finer("Do nothing");
    }

    public void push(String classname, String name, String desc)
    {
        LOGGER.finer("Do nothing");
    }

    public void pop(int line)
    {
        LOGGER.finer("Do nothing");
    }

    public void lockObject(int line, Object object)
    {
        LOGGER.finer("Do nothing");
    }

    public void unlockObject(int line, Object object)
    {
        LOGGER.finer("Do nothing");
    }

    public void readVolatile(int line, Object object, String field)
    {
        LOGGER.finer("Do nothing");
    }

    public void writeVolatile(int line, Object object, String field)
    {
        LOGGER.finer("Do nothing");
    }

    public void loadArray(int line, Object array, int index)
    {
        LOGGER.finer("Do nothing");
    }

    public void storeArray(int line, Object array, int index)
    {
        LOGGER.finer("Do nothing");
    }

    public void getField(int line, Object object, String name)
    {
        LOGGER.finer("Do nothing");
    }

    public void putField(int line, Object object, String name)
    {
        LOGGER.finer("Do nothing");
    }

    public void getStatic(int line, Class clazz, String name)
    {
        LOGGER.finer("Do nothing");
    }

    public void putStatic(int line, Class clazz, String name)
    {
        LOGGER.finer("Do nothing");
    }

    public void waitStart(int line, Object object) throws InterruptedException
    {
        LOGGER.finer("Do nothing");
    }

    public void waitStart(int line, Object object, long milliseconds) throws InterruptedException
    {
        LOGGER.finer("Do nothing");
    }

    public void waitStart(int line, Object object, long milliseconds, int nanoseconds) throws InterruptedException
    {
        LOGGER.finer("Do nothing");
    }

    public void notifyObject(int line, Object object)
    {
        LOGGER.finer("Do nothing");
    }

    public void notifyAllObject(int line, Object object)
    {
        LOGGER.finer("Do nothing");
    }
}
