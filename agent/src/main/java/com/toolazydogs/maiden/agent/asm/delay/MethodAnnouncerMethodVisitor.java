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
package com.toolazydogs.maiden.agent.asm.delay;

import java.util.Stack;
import java.util.logging.Logger;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;


/**
 *
 */
public class MethodAnnouncerMethodVisitor implements MarkableMethodVisitor
{
    private final static String CLASS_NAME = MethodAnnouncerMethodVisitor.class.getName();
    private final static Logger LOGGER = Logger.getLogger(CLASS_NAME);
    private final Stack<DelayedMethodVisitor> delayed = new Stack<DelayedMethodVisitor>();
    private final String clazz;
    private final String name;
    private final String desc;
    private final MarkableMethodVisitor visitor;
    private boolean mark = false;

    public MethodAnnouncerMethodVisitor(String clazz, String name, String desc, MarkableMethodVisitor visitor)
    {
        assert clazz != null;
        assert name != null;
        assert desc != null;
        assert visitor != null;

        this.clazz = clazz;
        this.name = name;
        this.desc = desc;
        this.visitor = visitor;
    }

    public AnnotationVisitor visitAnnotationDefault()
    {
        return (AnnotationVisitor)delayed.push(new DefaultAnnotationVisitor());
    }

    public AnnotationVisitor visitAnnotation(String desc, boolean visible)
    {
        return (AnnotationVisitor)delayed.push(new DefaultAnnotationVisitor(desc, visible));
    }

    public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible)
    {
        return (AnnotationVisitor)delayed.push(new DefaultAnnotationVisitor(parameter, desc, visible));
    }

    public void visitAttribute(final Attribute attribute)
    {
        delayed.push(new DelayedMethodVisitor()
        {
            public void flush(MethodVisitor methodVisitor, boolean mark)
            {
                methodVisitor.visitAttribute(attribute);
            }
        });
    }

    public void visitCode()
    {
        delayed.push(new DelayedMethodVisitor()
        {
            public void flush(MethodVisitor methodVisitor, boolean mark)
            {
                methodVisitor.visitCode();

                if (mark)
                {
                    methodVisitor.visitLdcInsn(clazz);
                    methodVisitor.visitLdcInsn(name);
                    methodVisitor.visitLdcInsn(desc);
                    methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, "com/toolazydogs/maiden/IronMaiden", "announceMethod", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");
                }
            }
        });
    }

    public void visitFrame(final int type, final int nLocal, final Object[] local, final int nStack, final Object[] stack)
    {
        delayed.push(new DelayedMethodVisitor()
        {
            public void flush(MethodVisitor methodVisitor, boolean mark)
            {
                methodVisitor.visitFrame(type, nLocal, local, nStack, stack);
            }
        });
    }

    public void visitInsn(final int opcode)
    {
        delayed.push(new DelayedMethodVisitor()
        {
            public void flush(MethodVisitor methodVisitor, boolean mark)
            {
                methodVisitor.visitInsn(opcode);
            }
        });
    }

    public void visitIntInsn(final int opcode, final int operand)
    {
        delayed.push(new DelayedMethodVisitor()
        {
            public void flush(MethodVisitor methodVisitor, boolean mark)
            {
                methodVisitor.visitIntInsn(opcode, operand);
            }
        });
    }

    public void visitVarInsn(final int opcode, final int var)
    {
        delayed.push(new DelayedMethodVisitor()
        {
            public void flush(MethodVisitor methodVisitor, boolean mark)
            {
                methodVisitor.visitVarInsn(opcode, var);
            }
        });
    }

    public void visitTypeInsn(final int opcode, final String type)
    {
        delayed.push(new DelayedMethodVisitor()
        {
            public void flush(MethodVisitor methodVisitor, boolean mark)
            {
                methodVisitor.visitTypeInsn(opcode, type);
            }
        });
    }

    public void visitFieldInsn(final int opcode, final String owner, final String name, final String desc)
    {
        delayed.push(new DelayedMethodVisitor()
        {
            public void flush(MethodVisitor methodVisitor, boolean mark)
            {
                methodVisitor.visitFieldInsn(opcode, owner, name, desc);
            }
        });
    }

    public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc)
    {
        delayed.push(new DelayedMethodVisitor()
        {
            public void flush(MethodVisitor methodVisitor, boolean mark)
            {
                methodVisitor.visitMethodInsn(opcode, owner, name, desc);
            }
        });
    }

    public void visitJumpInsn(final int opcode, final Label label)
    {
        delayed.push(new DelayedMethodVisitor()
        {
            public void flush(MethodVisitor methodVisitor, boolean mark)
            {
                methodVisitor.visitJumpInsn(opcode, label);
            }
        });
    }

    public void visitLabel(final Label label)
    {
        delayed.push(new DelayedMethodVisitor()
        {
            public void flush(MethodVisitor methodVisitor, boolean mark)
            {
                methodVisitor.visitLabel(label);
            }
        });
    }

    public void visitLdcInsn(final Object cst)
    {
        delayed.push(new DelayedMethodVisitor()
        {
            public void flush(MethodVisitor methodVisitor, boolean mark)
            {
                methodVisitor.visitLdcInsn(cst);
            }
        });
    }

    public void visitIincInsn(final int var, final int increment)
    {
        delayed.push(new DelayedMethodVisitor()
        {
            public void flush(MethodVisitor methodVisitor, boolean mark)
            {
                methodVisitor.visitIincInsn(var, increment);
            }
        });
    }

    public void visitTableSwitchInsn(final int min, final int max, final Label dflt, final Label[] labels)
    {
        delayed.push(new DelayedMethodVisitor()
        {
            public void flush(MethodVisitor methodVisitor, boolean mark)
            {
                methodVisitor.visitTableSwitchInsn(min, max, dflt, labels);
            }
        });
    }

    public void visitLookupSwitchInsn(final Label dflt, final int[] keys, final Label[] labels)
    {
        delayed.push(new DelayedMethodVisitor()
        {
            public void flush(MethodVisitor methodVisitor, boolean mark)
            {
                methodVisitor.visitLookupSwitchInsn(dflt, keys, labels);
            }
        });
    }

    public void visitMultiANewArrayInsn(final String desc, final int dims)
    {
        delayed.push(new DelayedMethodVisitor()
        {
            public void flush(MethodVisitor methodVisitor, boolean mark)
            {
                methodVisitor.visitMultiANewArrayInsn(desc, dims);
            }
        });
    }

    public void visitTryCatchBlock(final Label start, final Label end, final Label handler, final String type)
    {
        delayed.push(new DelayedMethodVisitor()
        {
            public void flush(MethodVisitor methodVisitor, boolean mark)
            {
                methodVisitor.visitTryCatchBlock(start, end, handler, type);
            }
        });
    }

    public void visitLocalVariable(final String name, final String desc, final String signature, final Label start, final Label end, final int index)
    {
        delayed.push(new DelayedMethodVisitor()
        {
            public void flush(MethodVisitor methodVisitor, boolean mark)
            {
                methodVisitor.visitLocalVariable(name, desc, signature, start, end, index);
            }
        });
    }

    public void visitLineNumber(final int line, final Label start)
    {
        delayed.push(new DelayedMethodVisitor()
        {
            public void flush(MethodVisitor methodVisitor, boolean mark)
            {
                methodVisitor.visitLineNumber(line, start);
            }
        });
    }

    public void visitMaxs(final int maxStack, final int maxLocals)
    {
        delayed.push(new DelayedMethodVisitor()
        {
            public void flush(MethodVisitor methodVisitor, boolean mark)
            {
                methodVisitor.visitMaxs(maxStack, maxLocals);
            }
        });
    }

    public void visitEnd()
    {
        delayed.push(new DelayedMethodVisitor()
        {
            public void flush(MethodVisitor methodVisitor, boolean mark)
            {
                methodVisitor.visitEnd();
            }
        });
        flush();
    }

    public void mark()
    {
        LOGGER.finest("Line announcer visitor marked");
        mark = true;
        visitor.mark();
    }

    private void flush()
    {
        LOGGER.entering(CLASS_NAME, "flush");

        for (DelayedMethodVisitor dmv : delayed) dmv.flush(visitor, mark);
        delayed.clear();
        mark = false;

        LOGGER.exiting(CLASS_NAME, "flush");
    }
}
