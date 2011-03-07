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
    public static void pushInteger(MethodVisitor methodVisitor, int value)
    {
        if (Byte.MIN_VALUE <= value && value <= Byte.MAX_VALUE) methodVisitor.visitIntInsn(Opcodes.BIPUSH, value);
        else if (Short.MIN_VALUE <= value && value <= Short.MAX_VALUE) methodVisitor.visitIntInsn(Opcodes.SIPUSH, value);
    }
}
