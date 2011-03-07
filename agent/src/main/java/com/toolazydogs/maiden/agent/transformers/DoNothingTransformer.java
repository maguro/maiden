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
package com.toolazydogs.maiden.agent.transformers;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Properties;
import java.util.logging.Logger;


/**
 * A simple do-nothing class file transformer.  It leaves classes unmodified.
 */
public class DoNothingTransformer implements ClassFileTransformer
{
    private final static String CLASS_NAME = DoNothingTransformer.class.getName();
    private final static Logger LOGGER = Logger.getLogger(CLASS_NAME);

    public DoNothingTransformer(Properties properties)
    {
    }

    /**
     * {@inheritDoc}
     */
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException
    {
        LOGGER.entering(CLASS_NAME, "transform", new Object[]{loader, className, classBeingRedefined, protectionDomain, classfileBuffer});

        LOGGER.exiting(CLASS_NAME, "transform", classfileBuffer);

        return classfileBuffer;
    }
}
