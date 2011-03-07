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

import java.util.logging.Logger;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

import com.toolazydogs.maiden.agent.asm.delay.LineAnnouncerMethodVisitor;
import com.toolazydogs.maiden.agent.asm.delay.MethodAnnouncerMethodVisitor;
import com.toolazydogs.maiden.agent.asm.delay.MethodVisitorAdaptor;


/**
 * An ASM class visitor.
 */
public class IronClassVisitor implements ClassVisitor
{
    private final static String CLASS_NAME = IronClassVisitor.class.getName();
    private final static Logger LOGGER = Logger.getLogger(CLASS_NAME);
    private final String clazz;
    private final ClassVisitor delegate;

    public IronClassVisitor(String clazz, ClassVisitor delegate)
    {
        assert clazz != null;
        assert delegate != null;

        this.clazz = clazz;
        this.delegate = delegate;
    }

    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) { delegate.visit(version, access, name, signature, superName, interfaces); }

    public void visitSource(String source, String debug) { delegate.visitSource(source, debug); }

    public void visitOuterClass(String owner, String name, String desc) { delegate.visitOuterClass(owner, name, desc); }

    public AnnotationVisitor visitAnnotation(String desc, boolean visible) { return delegate.visitAnnotation(desc, visible);}

    public void visitAttribute(Attribute attr) { delegate.visitAttribute(attr); }

    public void visitInnerClass(String name, String outerName, String innerName, int access) { delegate.visitInnerClass(name, outerName, innerName, access); }

    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) { return delegate.visitField(access, name, desc, signature, value); }

    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
    {
        return new IronMethodVisitor(
                new LineAnnouncerMethodVisitor(
                        new MethodAnnouncerMethodVisitor(clazz, name, desc,
                                                         new MethodVisitorAdaptor(
                                                                 delegate.visitMethod(access, name, desc, signature, exceptions)))));
    }

    public void visitEnd() { delegate.visitEnd(); }
}
