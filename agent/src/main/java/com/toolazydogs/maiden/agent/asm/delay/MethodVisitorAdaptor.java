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

import java.util.logging.Logger;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;


/**
 * Adapts an instance of {@link MethodVisitor} to implement {@link MarkableMethodVisitor}.
 */
public class MethodVisitorAdaptor implements MarkableMethodVisitor
{
    private final static String CLASS_NAME = MethodVisitorAdaptor.class.getName();
    private final static Logger LOGGER = Logger.getLogger(CLASS_NAME);
    private final MethodVisitor methodVisitor;

    public MethodVisitorAdaptor(MethodVisitor methodVisitor)
    {
        assert methodVisitor != null;

        this.methodVisitor = methodVisitor;
    }

    public AnnotationVisitor visitAnnotationDefault() {return methodVisitor.visitAnnotationDefault(); }

    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {return methodVisitor.visitAnnotation(desc, visible); }

    public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {return methodVisitor.visitParameterAnnotation(parameter, desc, visible); }

    public void visitAttribute(Attribute attr) { methodVisitor.visitAttribute(attr); }

    public void visitCode() { methodVisitor.visitCode(); }

    public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) { methodVisitor.visitFrame(type, nLocal, local, nStack, stack); }

    public void visitInsn(int opcode) { methodVisitor.visitInsn(opcode); }

    public void visitIntInsn(int opcode, int operand) { methodVisitor.visitIntInsn(opcode, operand); }

    public void visitVarInsn(int opcode, int var) { methodVisitor.visitVarInsn(opcode, var); }

    public void visitTypeInsn(int opcode, String type) { methodVisitor.visitTypeInsn(opcode, type); }

    public void visitFieldInsn(int opcode, String owner, String name, String desc) { methodVisitor.visitFieldInsn(opcode, owner, name, desc); }

    public void visitMethodInsn(int opcode, String owner, String name, String desc) { methodVisitor.visitMethodInsn(opcode, owner, name, desc); }

    public void visitJumpInsn(int opcode, Label label) { methodVisitor.visitJumpInsn(opcode, label); }

    public void visitLabel(Label label) { methodVisitor.visitLabel(label); }

    public void visitLdcInsn(Object cst) { methodVisitor.visitLdcInsn(cst); }

    public void visitIincInsn(int var, int increment) { methodVisitor.visitIincInsn(var, increment); }

    public void visitTableSwitchInsn(int min, int max, Label dflt, Label[] labels) { methodVisitor.visitTableSwitchInsn(min, max, dflt, labels); }

    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) { methodVisitor.visitLookupSwitchInsn(dflt, keys, labels); }

    public void visitMultiANewArrayInsn(String desc, int dims) { methodVisitor.visitMultiANewArrayInsn(desc, dims); }

    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) { methodVisitor.visitTryCatchBlock(start, end, handler, type); }

    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) { methodVisitor.visitLocalVariable(name, desc, signature, start, end, index); }

    public void visitLineNumber(int line, Label start) { methodVisitor.visitLineNumber(line, start); }

    public void visitMaxs(int maxStack, int maxLocals) { methodVisitor.visitMaxs(maxStack, maxLocals); }

    public void visitEnd() { methodVisitor.visitEnd(); }

    public void mark()
    {
        LOGGER.entering(CLASS_NAME, "mark");
        LOGGER.finest("Method ignored");
        LOGGER.exiting(CLASS_NAME, "mark");
    }
}
