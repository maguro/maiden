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

import com.toolazydogs.maiden.model.Line;


/**
 *
 */
public interface IronListener
{
    void push(String classname, String name, String desc);

    void pop(int line);

    void lockObject(int line, Object object);

    void unlockObject(int line, Object object);

    void readVolatile(int line, Object object, String field);

    void writeVolatile(int line, Object object, String field);

    void loadArray(int line, Object array, int index);

    void storeArray(int line, Object array, int index);

    void getField(int line, Object object, String name);

    void putField(int line, Object object, String name);

    void getStatic(int line, Class clazz, String name);

    void putStatic(int line, Class clazz, String name);

    void waitStart(int line, Object object) throws InterruptedException;

    void waitStart(int line, Object object, long milliseconds) throws InterruptedException;

    void waitStart(int line, Object object, long milliseconds, int nanoseconds) throws InterruptedException;

    void notifyObject(int line, Object object);

    void notifyAllObject(int line, Object object);
}
