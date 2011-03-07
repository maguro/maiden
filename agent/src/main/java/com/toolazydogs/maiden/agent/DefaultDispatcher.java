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
import java.security.ProtectionDomain;
import java.util.Properties;
import java.util.logging.Logger;

import com.toolazydogs.maiden.agent.api.Dispatcher;
import com.toolazydogs.maiden.agent.transformers.DoNothingTransformer;
import com.toolazydogs.maiden.agent.transformers.PrintClassNameTransformer;


/**
 *
 */
class DefaultDispatcher implements Dispatcher
{
    private final static String CLASS_NAME = DefaultDispatcher.class.getName();
    private final static Logger LOGGER = Logger.getLogger(CLASS_NAME);
    private final static ClassFileTransformer DO_NOTHING = new DoNothingTransformer();
    private final Properties properties;

    public DefaultDispatcher(Properties properties)
    {
        if (properties == null) throw new IllegalArgumentException("Properties cannot be null");
        this.properties = properties;
    }

    public ClassFileTransformer lookup(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain)
    {
        LOGGER.entering(CLASS_NAME, "lookup", new Object[]{loader, className, classBeingRedefined, protectionDomain});

        if (className.startsWith("com/toolazydogs/maiden")) return DO_NOTHING;

        ClassFileTransformer transformer = new PrintClassNameTransformer(properties);

        LOGGER.exiting(CLASS_NAME, "lookup", transformer);

        return transformer;
    }
}
