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
import java.util.logging.Level;
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
 * Use this class to insert code at the beginning and end of a method.
 * <p/>
 * An instance of {@link MethodVisitor} that wraps a method's code with a
 * <code>try/finally</code> pair.  It calls registered listeners, which
 * implement {@link BeginEndMethodListener}, before the try block to allow
 * listeners to insert code to be executed before the method's original
 * code.  When the <code>finally</code> segment is being generated the
 * listeners are again called to insert code to be executed before the thread
 * of control leaves the method.
 * <p/>
 * Listeners are also notified when a line number is encountered.
 */
public class BeginEndMethodVisitor implements MethodVisitor, Opcodes
{

    private final static String CLASS_NAME = BeginEndMethodVisitor.class.getName();
    private final static Logger LOGGER = Logger.getLogger(CLASS_NAME);
    private final List<BeginEndMethodListener> listeners = new ArrayList<BeginEndMethodListener>();
    private transient List<BeginEndMethodListener> reversed = null;
    private final LocalVariablesSorter lvs;
    private final MethodNode methodNode;
    private final MethodVisitor visitor;
    private State state = State.CLEARED;
    private final Label l7 = new Label();
    private Label start;
    private boolean sawCode = false;

    public BeginEndMethodVisitor(MethodVisitor visitor, int access, String name, String desc, String signature, String[] exceptions)
    {
        assert visitor != null;
        assert name != null;
        assert desc != null;

        if (LOGGER.isLoggable(Level.CONFIG))
        {
            LOGGER.config("visitor: " + visitor);
            LOGGER.config("access: " + access);
            LOGGER.config("name: " + name);
            LOGGER.config("desc: " + desc);
            LOGGER.config("signature: " + signature);
            for (String exception : exceptions) LOGGER.config("exception: " + exception);
        }

        this.visitor = visitor;

        // this MethodNode instance is used to fill in hte gaps of frames and maxes
        methodNode = new MethodNode(access, name, desc, signature, exceptions);

        // We use this instance to keep track of local variables that may need to be created.
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

    public AnnotationVisitor visitAnnotationDefault() { return lvs.visitAnnotationDefault(); }

    public AnnotationVisitor visitAnnotation(String desc, boolean visible) { return lvs.visitAnnotation(desc, visible); }

    public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) { return lvs.visitParameterAnnotation(parameter, desc, visible); }

    public void visitAttribute(Attribute attr) { lvs.visitAttribute(attr); }

    public void visitCode()
    {
        LOGGER.entering(CLASS_NAME, "visitCode");

        lvs.visitCode();

        for (BeginEndMethodListener listener : listeners) listener.begin(lvs);

        state = State.NEED_START_LABEL;

        LOGGER.exiting(CLASS_NAME, "visitCode");
    }

    public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack)
    {
    }

    public void visitInsn(int opcode)
    {
        LOGGER.entering(CLASS_NAME, "visitInsn", opcode);

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

                break;

            default:
                sawCode = true;
        }

        lvs.visitInsn(opcode);

        LOGGER.exiting(CLASS_NAME, "visitInsn");
    }

    public void visitIntInsn(int opcode, int operand)
    {
        LOGGER.entering(CLASS_NAME, "visitIntInsn", new Object[]{opcode, operand});

        flush();
        sawCode = true;
        lvs.visitIntInsn(opcode, operand);

        LOGGER.exiting(CLASS_NAME, "visitIntInsn");
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
        LOGGER.entering(CLASS_NAME, "visitTypeInsn", new Object[]{opcode, type});

        flush();
        sawCode = true;
        lvs.visitTypeInsn(opcode, type);

        LOGGER.exiting(CLASS_NAME, "visitTypeInsn");
    }

    public void visitFieldInsn(int opcode, String owner, String name, String desc)
    {
        LOGGER.entering(CLASS_NAME, "visitFieldInsn", new Object[]{opcode, owner, name, desc});

        flush();
        sawCode = true;
        lvs.visitFieldInsn(opcode, owner, name, desc);

        LOGGER.exiting(CLASS_NAME, "visitFieldInsn");
    }

    public void visitMethodInsn(int opcode, String owner, String name, String desc)
    {
        LOGGER.entering(CLASS_NAME, "visitMethodInsn", new Object[]{opcode, owner, name, desc});

        flush();
        sawCode = true;
        lvs.visitMethodInsn(opcode, owner, name, desc);

        LOGGER.exiting(CLASS_NAME, "visitMethodInsn");
    }

    public void visitJumpInsn(int opcode, Label label)
    {
        LOGGER.entering(CLASS_NAME, "visitJumpInsn", new Object[]{opcode, label});

        flush();
        sawCode = true;
        lvs.visitJumpInsn(opcode, label);

        LOGGER.exiting(CLASS_NAME, "visitJumpInsn");
    }

    public void visitLabel(Label label)
    {
        LOGGER.entering(CLASS_NAME, "visitLabel", label);

        flush();
        lvs.visitLabel(label);

        LOGGER.exiting(CLASS_NAME, "visitLabel");
    }

    public void visitLdcInsn(Object cst)
    {
        LOGGER.entering(CLASS_NAME, "visitLdcInsn", cst);

        flush();
        sawCode = true;
        lvs.visitLdcInsn(cst);

        LOGGER.exiting(CLASS_NAME, "visitLdcInsn");
    }

    public void visitIincInsn(int var, int increment)
    {
        LOGGER.entering(CLASS_NAME, "visitIincInsn", new Object[]{var, increment});

        flush();
        sawCode = true;
        lvs.visitIincInsn(var, increment);

        LOGGER.exiting(CLASS_NAME, "visitIincInsn");
    }

    public void visitTableSwitchInsn(int min, int max, Label dflt, Label[] labels)
    {
        LOGGER.entering(CLASS_NAME, "visitTableSwitchInsn", new Object[]{min, max, dflt, labels});

        flush();
        sawCode = true;
        lvs.visitTableSwitchInsn(min, max, dflt, labels);

        LOGGER.exiting(CLASS_NAME, "visitTableSwitchInsn");
    }

    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels)
    {
        LOGGER.entering(CLASS_NAME, "visitLookupSwitchInsn", new Object[]{dflt, keys, labels});

        flush();
        sawCode = true;
        lvs.visitLookupSwitchInsn(dflt, keys, labels);

        LOGGER.exiting(CLASS_NAME, "visitLookupSwitchInsn");
    }

    public void visitMultiANewArrayInsn(String desc, int dims)
    {
        LOGGER.entering(CLASS_NAME, "visitMultiANewArrayInsn", new Object[]{desc, dims});

        flush();
        sawCode = true;
        lvs.visitMultiANewArrayInsn(desc, dims);

        LOGGER.exiting(CLASS_NAME, "visitMultiANewArrayInsn");
    }

    public void visitTryCatchBlock(Label start, Label end, Label handler, String type)
    {
        LOGGER.entering(CLASS_NAME, "visitTryCatchBlock", new Object[]{start, end, handler, type});

        flush();
        lvs.visitTryCatchBlock(start, end, handler, type);

        LOGGER.exiting(CLASS_NAME, "visitTryCatchBlock");
    }

    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index)
    {
        LOGGER.entering(CLASS_NAME, "visitLocalVariable", new Object[]{name, desc, signature, start, end, index});

        flush();
        lvs.visitLocalVariable(name, desc, signature, start, end, index);

        LOGGER.exiting(CLASS_NAME, "visitLocalVariable");
    }

    public void visitLineNumber(int line, Label start)
    {
        LOGGER.entering(CLASS_NAME, "visitLineNumber", new Object[]{line, start});

        flush();
        for (BeginEndMethodListener listener : listeners) listener.line(line);
        lvs.visitLineNumber(line, start);

        LOGGER.exiting(CLASS_NAME, "visitLineNumber");
    }

    public void visitMaxs(int maxStack, int maxLocals)
    {
        LOGGER.entering(CLASS_NAME, "visitMaxs", new Object[]{maxStack, maxLocals});

        flush();

        LOGGER.exiting(CLASS_NAME, "visitMaxs");
    }

    public void visitEnd()
    {
        LOGGER.entering(CLASS_NAME, "visitEnd");

        flush();

        if (sawCode)
        {
            LOGGER.finest("Saw code");

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

        lvs.visitEnd();

        methodNode.accept(visitor);

        LOGGER.exiting(CLASS_NAME, "visitEnd");
    }

    /**
     * Delay flushing out first label for our try/finally block until the first
     * bit of code is encountered.
     */
    private void flush()
    {
        LOGGER.entering(CLASS_NAME, "flush");

        if (state == State.NEED_START_LABEL)
        {
            LOGGER.finest("Need start label");

            start = new Label();
            lvs.visitLabel(start);
            state = State.CLEARED;
        }

        LOGGER.exiting(CLASS_NAME, "flush");
    }

    private static enum State
    {
        NEED_START_LABEL, CLEARED
    }
}
