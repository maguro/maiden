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
 * @version $Revision: $ $Date: $
 */
public class IronAgent
{
    private final static String CLASS_NAME = IronAgent.class.getName();
    private final static Logger LOGGER = Logger.getLogger(CLASS_NAME);
    private static Instrumentation INSTRUMENTATION;

    public static void premain(String args, Instrumentation instrumentation)
    {
        INSTRUMENTATION = instrumentation;
    }

    public static void agentmain(String args, Instrumentation instrumentation) throws Exception
    {
        INSTRUMENTATION = instrumentation;
    }

    public static void initialize()
    {
        //Todo change body of created methods use File | Settings | File Templates.
    }
}
