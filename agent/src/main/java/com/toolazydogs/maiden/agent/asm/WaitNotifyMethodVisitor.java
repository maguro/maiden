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

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;


/**
 *
 */
public class WaitNotifyMethodVisitor implements MethodVisitor, Opcodes
{
    private final static String CLASS_NAME = WaitNotifyMethodVisitor.class.getName();
    private final static Logger LOGGER = Logger.getLogger(CLASS_NAME);
    private final MethodVisitor visitor;
    private State state = State.NONE;
    private int line;
    private long milliseconds;
    private int nanoseconds;

    public WaitNotifyMethodVisitor(MethodVisitor visitor)
    {
        this.visitor = visitor;
    }

    public AnnotationVisitor visitAnnotationDefault() { return visitor.visitAnnotationDefault(); }

    public AnnotationVisitor visitAnnotation(String desc, boolean visible) { return visitor.visitAnnotation(desc, visible); }

    public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) { return visitor.visitParameterAnnotation(parameter, desc, visible); }

    public void visitAttribute(Attribute attr) { visitor.visitAttribute(attr); }

    public void visitCode() { visitor.visitCode(); }

    public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack)
    {
        flush();
        visitor.visitFrame(type, nLocal, local, nStack, stack);
    }

    public void visitInsn(int opcode)
    {
        if (state == State.FOUND_ALOAD0 && (opcode == LCONST_0 || opcode == LCONST_1))
        {
            milliseconds = opcode - LCONST_0;
            state = State.FOUND_MILLISECONDS;
        }
        else if (state == State.FOUND_MILLISECONDS && (opcode >= ICONST_0 && opcode <= ICONST_5))
        {
            nanoseconds = opcode - ICONST_0;
            state = State.FOUND_NANOSECONDS;
        }
        else
        {
            flush();
            visitor.visitInsn(opcode);
        }
    }

    public void visitIntInsn(int opcode, int operand)
    {
        if (state == State.FOUND_MILLISECONDS && (opcode == Opcodes.BIPUSH || opcode == Opcodes.SIPUSH))
        {
            nanoseconds = opcode;
            state = State.FOUND_NANOSECONDS;
        }
        else
        {
            flush();
            visitor.visitIntInsn(opcode, operand);
        }
    }

    public void visitVarInsn(int opcode, int var)
    {
        if (state == State.NONE && opcode == ALOAD && var == 0)
        {
            state = State.FOUND_ALOAD0;
        }
        else
        {
            flush();
            visitor.visitVarInsn(opcode, var);
        }
    }

    public void visitTypeInsn(int opcode, String type)
    {
        flush();
        visitor.visitTypeInsn(opcode, type);
    }

    public void visitFieldInsn(int opcode, String owner, String name, String desc)
    {
        flush();
        visitor.visitFieldInsn(opcode, owner, name, desc);
    }

    public void visitMethodInsn(int opcode, String owner, String name, String desc)
    {
        if (opcode == INVOKEVIRTUAL)
        {
            boolean wait = "java/lang/Object".equals(owner) && "wait".equals(name);
            boolean notify = "java/lang/Object".equals(owner) && "notify".equals(name);
            boolean notifyAll = "java/lang/Object".equals(owner) && "notifyAll".equals(name);

            if (wait)
            {
                if (state == State.FOUND_ALOAD0)
                {
                    AsmUtils.push(visitor, line);
                    visitor.visitVarInsn(ALOAD, 0);
                    visitor.visitMethodInsn(INVOKESTATIC, "com/toolazydogs/maiden/IronMaiden", "waitStart", "(ILjava/lang/Object;)V");

                    state = State.NONE;
                }
                if (state == State.FOUND_MILLISECONDS)
                {
                    AsmUtils.push(visitor, line);
                    visitor.visitVarInsn(ALOAD, 0);
                    AsmUtils.push(visitor, milliseconds);
                    visitor.visitMethodInsn(INVOKESTATIC, "com/toolazydogs/maiden/IronMaiden", "waitStart", "(ILjava/lang/Object;J)V");

                    state = State.NONE;
                }
                else if (state == State.FOUND_NANOSECONDS)
                {
                    AsmUtils.push(visitor, line);
                    visitor.visitVarInsn(ALOAD, 0);
                    AsmUtils.push(visitor, milliseconds);
                    AsmUtils.push(visitor, nanoseconds);
                    visitor.visitMethodInsn(INVOKESTATIC, "com/toolazydogs/maiden/IronMaiden", "waitStart", "(ILjava/lang/Object;JI)V");

                    state = State.NONE;
                }
            }
            else if (notify)
            {
                flush();

                AsmUtils.push(visitor, line);
                visitor.visitVarInsn(ALOAD, 0);
                visitor.visitMethodInsn(INVOKESTATIC, "com/toolazydogs/maiden/IronMaiden", "notifyObject", "(ILjava/lang/Object;)V");
            }
            else if (notifyAll)
            {
                flush();

                AsmUtils.push(visitor, line);
                visitor.visitVarInsn(ALOAD, 0);
                visitor.visitMethodInsn(INVOKESTATIC, "com/toolazydogs/maiden/IronMaiden", "notifyAllObject", "(ILjava/lang/Object;)V");
            }
            else
            {
                flush();
                visitor.visitMethodInsn(opcode, owner, name, desc);
            }
        }
        else
        {
            flush();
            visitor.visitMethodInsn(opcode, owner, name, desc);
        }
    }

    public void visitJumpInsn(int opcode, Label label)
    {
        flush();
        visitor.visitJumpInsn(opcode, label);
    }

    public void visitLabel(Label label)
    {
        flush();
        visitor.visitLabel(label);
    }

    public void visitLdcInsn(Object cst)
    {
        if (state == State.FOUND_ALOAD0 && cst instanceof Long)
        {
            milliseconds = (Long)cst;
            state = State.FOUND_MILLISECONDS;
        }
        else if (state == State.FOUND_MILLISECONDS && cst instanceof Integer)
        {
            nanoseconds = (Integer)cst;
            state = State.FOUND_NANOSECONDS;
        }
        else
        {
            flush();
            visitor.visitLdcInsn(cst);
        }
    }

    public void visitIincInsn(int var, int increment)
    {
        flush();
        visitor.visitIincInsn(var, increment);
    }

    public void visitTableSwitchInsn(int min, int max, Label dflt, Label[] labels)
    {
        flush();
        visitor.visitTableSwitchInsn(min, max, dflt, labels);
    }

    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels)
    {
        flush();
        visitor.visitLookupSwitchInsn(dflt, keys, labels);
    }

    public void visitMultiANewArrayInsn(String desc, int dims)
    {
        flush();
        visitor.visitMultiANewArrayInsn(desc, dims);
    }

    public void visitTryCatchBlock(Label start, Label end, Label handler, String type)
    {
        flush();
        visitor.visitTryCatchBlock(start, end, handler, type);
    }

    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index)
    {
        flush();
        visitor.visitLocalVariable(name, desc, signature, start, end, index);
    }

    public void visitLineNumber(int line, Label start)
    {
        flush();
        this.line = line;
        visitor.visitLineNumber(line, start);
    }

    public void visitMaxs(int maxStack, int maxLocals)
    {
        flush();
        visitor.visitMaxs(maxStack, maxLocals);
    }

    public void visitEnd()
    {
        flush();
        visitor.visitEnd();
    }

    private void flush()
    {
        if (state == State.FOUND_ALOAD0)
        {
            visitor.visitVarInsn(ALOAD, 0);
        }
        else if (state == State.FOUND_MILLISECONDS)
        {
            visitor.visitVarInsn(ALOAD, 0);
            AsmUtils.push(visitor, milliseconds);
        }
        else if (state == State.FOUND_NANOSECONDS)
        {
            visitor.visitVarInsn(ALOAD, 0);
            AsmUtils.push(visitor, milliseconds);
            AsmUtils.push(visitor, nanoseconds);
        }
        state = State.NONE;
    }

    private enum State
    {
        NONE,
        FOUND_ALOAD0,
        FOUND_MILLISECONDS,
        FOUND_NANOSECONDS
    }
}
