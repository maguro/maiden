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

import java.lang.management.ManagementFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.tools.attach.VirtualMachine;


/**
 * A simple helper class that will load an agent after the JVM has started. One
 * must provide a file path to the agent jar to accomplish this.
 * <p/>
 * Note: This loader relies on Sun's JVM class {@link com.sun.tools.attach.VirtualMachine}.
 */
final public class IronAgentLoader
{
    private final static String CLASS_NAME = IronAgentLoader.class.getName();
    private final static Logger LOGGER = Logger.getLogger(CLASS_NAME);

    /**
     * Load an agent after the JVM has started.
     * <p/>
     * One must provide a file path to the agent jar to accomplish this.
     *
     * @param jarFilePath the path of the agent jar to install
     * @param agentArgs   the args to pass to the agent's {@link IronAgent#agentmain(String, java.lang.instrument.Instrumentation)} method.
     */
    public static void loadAgent(String jarFilePath, String agentArgs)
    {
        LOGGER.entering(CLASS_NAME, "loadAgent", jarFilePath);

        if (jarFilePath == null || jarFilePath.length() == 0) throw new IllegalArgumentException("Jar file path cannot be null or empty");
        if (agentArgs == null) throw new IllegalArgumentException("Agent's args cannot be null");

        String nameOfRunningVM = ManagementFactory.getRuntimeMXBean().getName();
        int i = nameOfRunningVM.indexOf('@');
        String pid = nameOfRunningVM.substring(0, i);

        if (LOGGER.isLoggable(Level.FINER)) LOGGER.finer("Found pid " + pid);

        try
        {
            LOGGER.finer("Attaching virtual machine");

            VirtualMachine vm = VirtualMachine.attach(pid);

            LOGGER.finer("Loading agent");

            vm.loadAgent(jarFilePath, agentArgs);

            LOGGER.finer("Agent loaded");

            vm.detach();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

        LOGGER.exiting(CLASS_NAME, "loadAgent");
    }
}
