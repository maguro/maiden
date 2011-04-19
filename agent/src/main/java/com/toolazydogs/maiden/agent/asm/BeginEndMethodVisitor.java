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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.LocalVariablesSorter;
import org.objectweb.asm.tree.MethodNode;


/**
 *
 */
public class BeginEndMethodVisitor implements MethodVisitor, Opcodes
{
    private final static int CLEARED = 0;
    private final static int NEED_START_LABEL = 1;

    private final static String CLASS_NAME = BeginEndMethodVisitor.class.getName();
    private final static Logger LOGGER = Logger.getLogger(CLASS_NAME);
    private final List<BeginEndMethodListener> listeners = new ArrayList<BeginEndMethodListener>();
    private transient List<BeginEndMethodListener> reversed = null;
    private final LocalVariablesSorter lvs;
    private final MethodNode methodNode;
    private final MethodVisitor visitor;
    private int state = CLEARED;
    private final Label l7 = new Label();
    private Label start;
    private boolean sawCode = false;

    public BeginEndMethodVisitor(MethodVisitor visitor, String clazz, int access, String name, String desc, String signature, String[] exceptions)
    {
        assert visitor != null;
        assert clazz != null;
        assert name != null;
        assert desc != null;

        this.visitor = visitor;

        methodNode = new MethodNode(access, name, desc, signature, exceptions);
        lvs = new LocalVariablesSorter(access, desc, methodNode);
    }

    public List<BeginEndMethodListener> getListeners()
    {
        reversed = null;
        return listeners;
    }

    protected List<BeginEndMethodListener> getReversed()
    {
        if (reversed == null)
        {
            reversed = new ArrayList<BeginEndMethodListener>(listeners);
            Collections.reverse(reversed);
        }
        return reversed;
    }

    public AnnotationVisitor visitAnnotationDefault() { return lvs.visitAnnotationDefault();}

    public AnnotationVisitor visitAnnotation(String desc, boolean visible) { return lvs.visitAnnotation(desc, visible);}

    public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {return lvs.visitParameterAnnotation(parameter, desc, visible);}

    public void visitAttribute(Attribute attr) {lvs.visitAttribute(attr);}

    public void visitCode()
    {
        LOGGER.entering(CLASS_NAME, "visitCode");

        lvs.visitCode();

        for (BeginEndMethodListener listener : listeners) listener.begin(lvs);

        state = NEED_START_LABEL;

        LOGGER.exiting(CLASS_NAME, "visitCode");
    }

    public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack)
    {
    }

    public void visitInsn(int opcode)
    {
        LOGGER.entering(CLASS_NAME, "visitInsn", new Object[]{opcode});
        flush();

        switch (opcode)
        {
            case IRETURN:
            case LRETURN:
            case FRETURN:
            case DRETURN:
            case ARETURN:
            case RETURN:

                if (sawCode)
                {
                    Label end = new Label();
                    lvs.visitLabel(end);
                    lvs.visitTryCatchBlock(start, end, l7, null);
                }

                for (BeginEndMethodListener listener : getReversed()) listener.end(lvs);

                start = null;
                state = NEED_START_LABEL;

                break;

            default:
                sawCode = true;
        }

        lvs.visitInsn(opcode);

        LOGGER.exiting(CLASS_NAME, "visitInsn");
    }

    public void visitIntInsn(int opcode, int operand)
    {
        flush();
        sawCode = true;
        lvs.visitIntInsn(opcode, operand);
    }

    public void visitVarInsn(int opcode, int var)
    {
        LOGGER.entering(CLASS_NAME, "visitVarInsn", new Object[]{opcode, var});

        flush();
        sawCode = true;
        lvs.visitVarInsn(opcode, var);

        LOGGER.exiting(CLASS_NAME, "visitVarInsn");
    }

    public void visitTypeInsn(int opcode, String type)
    {
        flush();
        sawCode = true;
        lvs.visitTypeInsn(opcode, type);
    }

    public void visitFieldInsn(int opcode, String owner, String name, String desc)
    {
        flush();
        sawCode = true;
        lvs.visitFieldInsn(opcode, owner, name, desc);
    }

    public void visitMethodInsn(int opcode, String owner, String name, String desc)
    {
        flush();
        sawCode = true;
        lvs.visitMethodInsn(opcode, owner, name, desc);
    }

    public void visitJumpInsn(int opcode, Label label)
    {
        flush();
        sawCode = true;
        lvs.visitJumpInsn(opcode, label);
    }

    public void visitLabel(Label label)
    {
        if (state == NEED_START_LABEL)
        {
            start = new Label();
            lvs.visitLabel(start);
            state = CLEARED;
        }
        lvs.visitLabel(label);
    }

    public void visitLdcInsn(Object cst)
    {
        flush();
        sawCode = true;
        lvs.visitLdcInsn(cst);
    }

    public void visitIincInsn(int var, int increment)
    {
        flush();
        sawCode = true;
        lvs.visitIincInsn(var, increment);
    }

    public void visitTableSwitchInsn(int min, int max, Label dflt, Label[] labels)
    {
        flush();
        sawCode = true;
        lvs.visitTableSwitchInsn(min, max, dflt, labels);
    }

    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels)
    {
        flush();
        sawCode = true;
        lvs.visitLookupSwitchInsn(dflt, keys, labels);
    }

    public void visitMultiANewArrayInsn(String desc, int dims)
    {
        flush();
        sawCode = true;
        lvs.visitMultiANewArrayInsn(desc, dims);
    }

    public void visitTryCatchBlock(Label start, Label end, Label handler, String type)
    {
        flush();
        lvs.visitTryCatchBlock(start, end, handler, type);
    }

    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index)
    {
        flush();
        lvs.visitLocalVariable(name, desc, signature, start, end, index);
    }

    public void visitLineNumber(int line, Label start)
    {
        flush();
        for (BeginEndMethodListener listener : listeners) listener.line(line);
        lvs.visitLineNumber(line, start);
    }

    public void visitMaxs(int maxStack, int maxLocals)
    {
        flush();

        if (sawCode)
        {
            final Label l13 = new Label();
            final int local = lvs.newLocal(Type.getType(Throwable.class));

            lvs.visitTryCatchBlock(l7, l13, l7, null);

            lvs.visitLabel(l7);
            lvs.visitVarInsn(ASTORE, local);
            lvs.visitLabel(l13);

            for (BeginEndMethodListener listener : getReversed()) listener.end(lvs);

            lvs.visitVarInsn(ALOAD, local);
            lvs.visitInsn(ATHROW);
        }
    }

    public void visitEnd()
    {
        lvs.visitEnd();

        methodNode.accept(visitor);
    }

    private void flush()
    {
        if (state == NEED_START_LABEL)
        {
            start = new Label();
            lvs.visitLabel(start);
            state = CLEARED;
        }
    }
}
