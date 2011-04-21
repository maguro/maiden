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
package com.toolazydogs.maiden.agent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.logging.Logger;

import org.objectweb.asm.Opcodes;

import com.toolazydogs.maiden.agent.api.Dispatcher;


/**
 * The root class transformer for the iron maiden agent.  This class is a
 * simple wrapper to a dispatcher.
 */
public final class IronTransformer implements ClassFileTransformer, Opcodes
{
    private final static String CLASS_NAME = IronTransformer.class.getName();
    private final static Logger LOGGER = Logger.getLogger(CLASS_NAME);
    private final Dispatcher dispatcher;
    private final boolean nativeMethodPrefixSupported;

    public IronTransformer(Dispatcher dispatcher)
    {
        this(dispatcher, false);
    }

    public IronTransformer(Dispatcher dispatcher, boolean nativeMethodPrefixSupported)
    {
        assert dispatcher != null;

        this.dispatcher = dispatcher;
        this.nativeMethodPrefixSupported = nativeMethodPrefixSupported;
    }

    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException
    {
        LOGGER.entering(CLASS_NAME, "transform", new Object[]{loader, className, classBeingRedefined, protectionDomain, classfileBuffer});

        ClassFileTransformer transformer = dispatcher.lookup(loader, className, classBeingRedefined, protectionDomain, nativeMethodPrefixSupported);

        byte[] results = transformer.transform(loader, className, classBeingRedefined, protectionDomain, classfileBuffer);

        LOGGER.exiting(CLASS_NAME, "transform", results);

        return results;
    }
}
