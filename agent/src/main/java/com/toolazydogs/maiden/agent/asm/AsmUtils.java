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
import org.objectweb.asm.Opcodes;


/**
 *
 */
public class AsmUtils
{
    /**
     * Generates the instruction to push the given value on the stack.
     *
     * @param methodVisitor the visitor to which to send the instruction
     * @param value         the value to be pushed on the stack.
     */
    public static void push(MethodVisitor methodVisitor, boolean value)
    {
        push(methodVisitor, value ? 1 : 0);
    }

    /**
     * Generates the instruction to push the given value on the stack.
     *
     * @param methodVisitor the visitor to which to send the instruction
     * @param value         the value to be pushed on the stack.
     */
    public static void push(MethodVisitor methodVisitor, int value)
    {
        if (value >= -1 && value <= 5)
        {
            methodVisitor.visitInsn(Opcodes.ICONST_0 + value);
        }
        else if (value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE)
        {
            methodVisitor.visitIntInsn(Opcodes.BIPUSH, value);
        }
        else if (value >= Short.MIN_VALUE && value <= Short.MAX_VALUE)
        {
            methodVisitor.visitIntInsn(Opcodes.SIPUSH, value);
        }
        else
        {
            methodVisitor.visitLdcInsn(value);
        }
    }

    private AsmUtils() { }
}
