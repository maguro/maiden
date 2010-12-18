/**
 *
 * Copyright 2010 (C) The original author or authors
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
package com.toolazydogs.maiden.agent;

import java.util.logging.Logger;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;


/**
 * An ASM class visitor.
 */
public class IronClassVisitor implements ClassVisitor
{
    private final static String CLASS_NAME = IronClassVisitor.class.getName();
    private final static Logger LOGGER = Logger.getLogger(CLASS_NAME);

    public void visit(int i, int i1, String s, String s1, String s2, String[] strings)
    {
        //Todo change body of implemented methods use File | Settings | File Templates.
    }

    public void visitSource(String s, String s1)
    {
        //Todo change body of implemented methods use File | Settings | File Templates.
    }

    public void visitOuterClass(String s, String s1, String s2)
    {
        //Todo change body of implemented methods use File | Settings | File Templates.
    }

    public AnnotationVisitor visitAnnotation(String s, boolean b)
    {
        return null;  //Todo change body of implemented methods use File | Settings | File Templates.
    }

    public void visitAttribute(Attribute attribute)
    {
        //Todo change body of implemented methods use File | Settings | File Templates.
    }

    public void visitInnerClass(String s, String s1, String s2, int i)
    {
        //Todo change body of implemented methods use File | Settings | File Templates.
    }

    public FieldVisitor visitField(int i, String s, String s1, String s2, Object o)
    {
        return null;  //Todo change body of implemented methods use File | Settings | File Templates.
    }

    public MethodVisitor visitMethod(int i, String s, String s1, String s2, String[] strings)
    {
        return null;  //Todo change body of implemented methods use File | Settings | File Templates.
    }

    public void visitEnd()
    {
        //Todo change body of implemented methods use File | Settings | File Templates.
    }
}
