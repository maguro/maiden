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
package com.toolazydogs.maiden.agent.asm;

import java.util.logging.Logger;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;


/**
 *
 */
public class MonitorMethodVisitor extends DelegateMethodVisitor implements Opcodes
{
    private final static String CLASS_NAME = MonitorMethodVisitor.class.getName();
    private final static Logger LOGGER = Logger.getLogger(CLASS_NAME);
    private int line;


    public MonitorMethodVisitor(MethodVisitor visitor)
    {
        super(visitor);
    }

    public void visitInsn(int opcode)
    {
        LOGGER.entering(CLASS_NAME, "visitInsn", opcode);

        getVisitor().visitInsn(opcode);

        switch (opcode)
        {
            case MONITORENTER:
                AsmUtils.pushInteger(getVisitor(), line);
                getVisitor().visitIntInsn(ALOAD, 0);
                getVisitor().visitMethodInsn(INVOKESTATIC, "com/toolazydogs/maiden/IronMaiden", "lockObject", "(ILjava/lang/Object;)V");
                break;

            case MONITOREXIT:
                AsmUtils.pushInteger(getVisitor(), line);
                getVisitor().visitIntInsn(ALOAD, 0);
                getVisitor().visitMethodInsn(INVOKESTATIC, "com/toolazydogs/maiden/IronMaiden", "unlockObject", "(ILjava/lang/Object;)V");
                break;

            default:
        }

        LOGGER.exiting(CLASS_NAME, "visitInsn");
    }

    @Override
    public void visitLineNumber(int line, Label start)
    {
        this.line = line;

        getVisitor().visitLineNumber(line, start);
    }
}
