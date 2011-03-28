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

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;


/**
 *
 */
public class DelegateMethodVisitor implements MethodVisitor
{
    private final MethodVisitor visitor;

    public DelegateMethodVisitor(MethodVisitor visitor)
    {
        assert visitor != null;

        this.visitor = visitor;
    }

    public MethodVisitor getVisitor()
    {
        return visitor;
    }

    public AnnotationVisitor visitAnnotationDefault() { return visitor.visitAnnotationDefault(); }

    public AnnotationVisitor visitAnnotation(String desc, boolean visible) { return visitor.visitAnnotation(desc, visible); }

    public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) { return visitor.visitParameterAnnotation(parameter, desc, visible); }

    public void visitAttribute(Attribute attr) { visitor.visitAttribute(attr); }

    public void visitCode() { visitor.visitCode(); }

    public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) { visitor.visitFrame(type, nLocal, local, nStack, stack); }

    public void visitInsn(int opcode) { visitor.visitInsn(opcode); }

    public void visitIntInsn(int opcode, int operand) { visitor.visitIntInsn(opcode, operand); }

    public void visitVarInsn(int opcode, int var) { visitor.visitVarInsn(opcode, var); }

    public void visitTypeInsn(int opcode, String type) { visitor.visitTypeInsn(opcode, type); }

    public void visitFieldInsn(int opcode, String owner, String name, String desc) { visitor.visitFieldInsn(opcode, owner, name, desc); }

    public void visitMethodInsn(int opcode, String owner, String name, String desc) { visitor.visitMethodInsn(opcode, owner, name, desc); }

    public void visitJumpInsn(int opcode, Label label) { visitor.visitJumpInsn(opcode, label); }

    public void visitLabel(Label label) { visitor.visitLabel(label); }

    public void visitLdcInsn(Object cst) { visitor.visitLdcInsn(cst); }

    public void visitIincInsn(int var, int increment) { visitor.visitIincInsn(var, increment); }

    public void visitTableSwitchInsn(int min, int max, Label dflt, Label[] labels) { visitor.visitTableSwitchInsn(min, max, dflt, labels); }

    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) { visitor.visitLookupSwitchInsn(dflt, keys, labels); }

    public void visitMultiANewArrayInsn(String desc, int dims) { visitor.visitMultiANewArrayInsn(desc, dims); }

    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) { visitor.visitTryCatchBlock(start, end, handler, type); }

    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) { visitor.visitLocalVariable(name, desc, signature, start, end, index); }

    public void visitLineNumber(int line, Label start) { visitor.visitLineNumber(line, start); }

    public void visitMaxs(int maxStack, int maxLocals) { visitor.visitMaxs(maxStack, maxLocals); }

    public void visitEnd() { visitor.visitEnd(); }
}
