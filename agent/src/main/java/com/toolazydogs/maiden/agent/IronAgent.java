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
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.toolazydogs.maiden.InMemoryDeadlockListener;
import com.toolazydogs.maiden.InMemoryReferenceListener;
import com.toolazydogs.maiden.IronMaiden;
import com.toolazydogs.maiden.agent.api.Dispatcher;
import com.toolazydogs.maiden.api.IronContext;


/**
 * An agent that registers the Iron Maiden's class transformer.
 */
final public class IronAgent
{
    public final static String NATIVE_METHOD_PREFIX = "com_toolazydogs_maiden_";
    public final static String DISPATCHER_CLASS = "dispatcher";
    private final static String CLASS_NAME = IronAgent.class.getName();
    private final static Logger LOGGER = Logger.getLogger(CLASS_NAME);

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

        sharedmain(agentArgs, instrumentation);

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

        sharedmain(agentArgs, instrumentation);

        LOGGER.exiting(CLASS_NAME, "agentmain");
    }

    /**
     * @param agentArgs       arguments for the agent
     * @param instrumentation provides services needed to instrument Java programming language code
     */
    @SuppressWarnings({"unchecked"})
    public static void sharedmain(String agentArgs, Instrumentation instrumentation)
    {
        LOGGER.entering(CLASS_NAME, "sharedmain", new Object[]{agentArgs, instrumentation});

        String dn = DefaultDispatcher.class.getName();
        Properties properties = new Properties();
        if (agentArgs != null)
        {
            for (String arg : agentArgs.split(","))
            {
                String[] keyValue = arg.split("=");
                if (keyValue.length == 2)
                {
                    if (DISPATCHER_CLASS.equalsIgnoreCase(keyValue[0])) dn = keyValue[1];
                    else properties.put(keyValue[0], keyValue[1]);
                }
                else
                {
                    LOGGER.warning("Argument not valid format " + arg);
                }
            }
        }

        IronContext context = IronMaiden.allocateContext();
        context.addListener(new InMemoryReferenceListener());
        context.addListener(new InMemoryDeadlockListener());

        try
        {
            Class<Dispatcher> dc;
            ClassLoader dcl = IronAgent.class.getClassLoader();
            if (dcl == null)
            {
                dc = (Class<Dispatcher>)Class.forName(dn);
            }
            else
            {
                dc = (Class<Dispatcher>)dcl.loadClass(dn);
            }

            Constructor<Dispatcher> constructor = dc.getConstructor(Properties.class);
            Dispatcher dispatcher = constructor.newInstance(properties);

            boolean nativeMethodPrefixSupported = instrumentation.isNativeMethodPrefixSupported();
            ClassFileTransformer transformer = new IronTransformer(dispatcher, nativeMethodPrefixSupported);
            instrumentation.addTransformer(transformer);

            if (nativeMethodPrefixSupported)
            {
                instrumentation.setNativeMethodPrefix(transformer, NATIVE_METHOD_PREFIX);
            }
        }
        catch (ClassNotFoundException e)
        {
            LOGGER.log(Level.SEVERE, "Unable to find class " + dn, e);
            throw new RuntimeException("Unable to find class " + dn, e);
        }
        catch (NoSuchMethodException e)
        {
            LOGGER.log(Level.SEVERE, "Unable to construct " + dn, e);
            throw new RuntimeException("Unable to construct " + dn, e);
        }
        catch (InvocationTargetException e)
        {
            LOGGER.log(Level.SEVERE, "Unable to construct " + dn, e);
            throw new RuntimeException("Unable to construct " + dn, e);
        }
        catch (InstantiationException e)
        {
            LOGGER.log(Level.SEVERE, "Unable to instantiate " + dn, e);
            throw new RuntimeException("Unable to instantiate " + dn, e);
        }
        catch (IllegalAccessException e)
        {
            LOGGER.log(Level.SEVERE, "Unable to instantiate " + dn, e);
            throw new RuntimeException("Unable to instantiate " + dn, e);
        }

        LOGGER.exiting(CLASS_NAME, "sharedmain");
    }
}
