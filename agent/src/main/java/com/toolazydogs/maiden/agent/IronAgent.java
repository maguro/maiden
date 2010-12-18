/**
 *
 * Copyright 2010 (C) The original author or authors
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

import java.lang.instrument.Instrumentation;
import java.util.logging.Logger;


/**
 * An agent that registers the Iron Maiden's class transformer.
 */
public class IronAgent
{
    private final static String CLASS_NAME = IronAgent.class.getName();
    private final static Logger LOGGER = Logger.getLogger(CLASS_NAME);
    private static Instrumentation INSTRUMENTATION;

    /**
     * After the Java Virtual Machine (JVM) has initialized, the premain method
     * will be called in the order the agents were specified, then the real
     * application main method will be called. Each premain method must return
     * in order for the startup sequence to proceed.
     *
     * @param agentArgs       the options passed on the javaagent commandline
     * @param instrumentation provides services needed to instrument Java programming language code
     */
    public static void premain(String agentArgs, Instrumentation instrumentation)
    {
        LOGGER.entering(CLASS_NAME, "premain", new Object[]{agentArgs, instrumentation});

        INSTRUMENTATION = instrumentation;

        instrumentation.addTransformer(new IronTransformer());

        LOGGER.exiting(CLASS_NAME, "premain");
    }

    /**
     * This method is called after the JVM has been started and the application main method was called.
     *
     * @param agentArgs       the options passed when loading the agent into the VM at runtime, see {@link IronAgentLoader#loadAgent(String, String)}
     * @param instrumentation provides services needed to instrument Java programming language code
     */
    public static void agentmain(String agentArgs, Instrumentation instrumentation)
    {
        LOGGER.entering(CLASS_NAME, "agentmain", new Object[]{agentArgs, instrumentation});

        INSTRUMENTATION = instrumentation;

        instrumentation.addTransformer(new IronTransformer());

        LOGGER.exiting(CLASS_NAME, "agentmain");
    }
}
