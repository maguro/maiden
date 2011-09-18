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
package com.toolazydogs.maiden.agent.asm;

import org.objectweb.asm.MethodVisitor;


/**
 * Implementations of this interface are notified when the beginning of a
 * method has been encountered and <em>before</em> code that forces a thread
 * to leave a method is executed.  This allows the listener to inject code
 * before a method's code is executed and before the execution thread leaves
 * the method.
 * <p/>
 * Helpful when there's different instructions to inject depending if the
 * method being visisted is static or regular.
 */
public class BeginEndMethodListener
{
    protected int line;

    /**
     * Beginning of method has been encountered.
     *
     * @param visitor an instance of {@link MethodVisitor} that can be used to inject code
     */
    public void begin(MethodVisitor visitor)
    {
    }

    /**
     * The current line number encountered.
     *
     * @param line the current line number encountered
     */
    public void line(int line)
    {
        this.line = line;
    }

    /**
     * The end of the method is about to be visited.
     *
     * @param visitor an instance of {@link MethodVisitor} that can be used to inject code
     */
    public void end(MethodVisitor visitor)
    {
    }
}
