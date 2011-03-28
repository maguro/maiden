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

import java.util.logging.Logger;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;


/**
 *
 */
public class PushPopMethodVisitor extends DelegateMethodVisitor implements Opcodes
{
    private final static String CLASS_NAME = PushPopMethodVisitor.class.getName();
    private final static Logger LOGGER = Logger.getLogger(CLASS_NAME);
    private final String clazz;
    private final String name;
    private final String desc;
    private int line;

    public PushPopMethodVisitor(MethodVisitor visitor, String clazz, String name, String desc)
    {
        super(visitor);

        assert clazz != null;
        assert name != null;
        assert desc != null;

        this.clazz = clazz;
        this.name = name;
        this.desc = desc;
    }

    @Override
    public void visitCode()
    {
        LOGGER.entering(CLASS_NAME, "visitCode");

        getVisitor().visitCode();

        getVisitor().visitLdcInsn(clazz);
        getVisitor().visitLdcInsn(name);
        getVisitor().visitLdcInsn(desc);
        getVisitor().visitMethodInsn(INVOKESTATIC, "com/toolazydogs/maiden/IronMaiden", "push", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");

        LOGGER.exiting(CLASS_NAME, "visitCode");
    }

    @Override
    public void visitInsn(int opcode)
    {
        LOGGER.entering(CLASS_NAME, "visitInsn", new Object[]{opcode});

        switch (opcode)
        {
            case IRETURN:
            case LRETURN:
            case FRETURN:
            case DRETURN:
            case ARETURN:
            case RETURN:
            case ATHROW:
                AsmUtils.pushInteger(getVisitor(), line);
                getVisitor().visitMethodInsn(INVOKESTATIC, "com/toolazydogs/maiden/IronMaiden", "pop", "(I)V");
                break;

            default:
        }

        getVisitor().visitInsn(opcode);

        LOGGER.exiting(CLASS_NAME, "visitInsn");
    }

    @Override
    public void visitVarInsn(int opcode, int var)
    {
        LOGGER.entering(CLASS_NAME, "visitVarInsn", new Object[]{opcode, var});

        switch (opcode)
        {
            case RET:
                AsmUtils.pushInteger(getVisitor(), line);
                getVisitor().visitMethodInsn(INVOKESTATIC, "com/toolazydogs/maiden/IronMaiden", "pop", "(I)V");
                break;

            default:
        }

        getVisitor().visitVarInsn(opcode, var);

        LOGGER.exiting(CLASS_NAME, "visitVarInsn");
    }

    @Override
    public void visitLineNumber(int line, Label start)
    {
        this.line = line;

        getVisitor().visitLineNumber(line, start);
    }
}
