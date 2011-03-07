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

import java.util.Stack;
import java.util.logging.Logger;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.toolazydogs.maiden.agent.asm.delay.DelayedMethodVisitor;
import com.toolazydogs.maiden.agent.asm.delay.MarkableMethodVisitor;


/**
 *
 */
public class IronMethodVisitor implements MethodVisitor, Opcodes
{
    private final static String CLASS_NAME = IronMethodVisitor.class.getName();
    private final static Logger LOGGER = Logger.getLogger(CLASS_NAME);
    private final Stack<DelayedMethodVisitor> delayed = new Stack<DelayedMethodVisitor>();
    private final MarkableMethodVisitor delegate;


    public IronMethodVisitor(MarkableMethodVisitor delegate)
    {
        assert delegate != null;

        this.delegate = delegate;
    }

    public AnnotationVisitor visitAnnotationDefault() { return delegate.visitAnnotationDefault(); }

    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {return delegate.visitAnnotation(desc, visible); }

    public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {return delegate.visitParameterAnnotation(parameter, desc, visible); }

    public void visitAttribute(Attribute attr) { delegate.visitAttribute(attr); }

    public void visitCode() { delegate.visitCode(); }

    public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) { delegate.visitFrame(type, nLocal, local, nStack, stack); }

    public void visitInsn(int opcode)
    {
        delegate.visitInsn(opcode);

        switch (opcode)
        {
            case IALOAD:
            case LALOAD:
            case FALOAD:
            case DALOAD:
            case AALOAD:
            case BALOAD:
            case CALOAD:
            case SALOAD:
            case IASTORE:
            case LASTORE:
            case FASTORE:
            case DASTORE:
            case AASTORE:
            case BASTORE:
            case CASTORE:
            case SASTORE:
                delegate.mark();
                break;

            case MONITORENTER:
                delegate.mark();
                break;

            case MONITOREXIT:
                delegate.mark();
                break;

            default:
        }
    }

    public void visitIntInsn(int opcode, int operand) { delegate.visitIntInsn(opcode, operand); }

    public void visitVarInsn(int opcode, int var) { delegate.visitVarInsn(opcode, var); }

    public void visitTypeInsn(int opcode, String type) { delegate.visitTypeInsn(opcode, type); }

    public void visitFieldInsn(int opcode, String owner, String name, String desc)
    {
        delegate.visitFieldInsn(opcode, owner, name, desc);
        delegate.mark();
    }

    public void visitMethodInsn(int opcode, String owner, String name, String desc)
    {
        delegate.visitMethodInsn(opcode, owner, name, desc);
        delegate.mark();
    }

    public void visitJumpInsn(int opcode, Label label) { delegate.visitJumpInsn(opcode, label); }

    public void visitLabel(Label label) { delegate.visitLabel(label); }

    public void visitLdcInsn(Object cst) { delegate.visitLdcInsn(cst); }

    public void visitIincInsn(int var, int increment) { delegate.visitIincInsn(var, increment); }

    public void visitTableSwitchInsn(int min, int max, Label dflt, Label[] labels) { delegate.visitTableSwitchInsn(min, max, dflt, labels); }

    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) { delegate.visitLookupSwitchInsn(dflt, keys, labels); }

    public void visitMultiANewArrayInsn(String desc, int dims) { delegate.visitMultiANewArrayInsn(desc, dims); }

    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) { delegate.visitTryCatchBlock(start, end, handler, type); }

    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) { delegate.visitLocalVariable(name, desc, signature, start, end, index); }

    public void visitLineNumber(int line, Label start) { delegate.visitLineNumber(line, start); }

    public void visitMaxs(int maxStack, int maxLocals) { delegate.visitMaxs(maxStack, maxLocals); }

    public void visitEnd() { delegate.visitEnd(); }
}
