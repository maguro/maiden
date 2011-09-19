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

import static com.toolazydogs.maiden.agent.asm.AsmUtils.push;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.toolazydogs.maiden.agent.IronAgent;


/**
 * An ASM class visitor.
 */
public class IronClassVisitor implements ClassVisitor, Opcodes
{
    private final static String CLASS_NAME = IronClassVisitor.class.getName();
    private final static Logger LOGGER = Logger.getLogger(CLASS_NAME);
    private final String clazz;
    private final boolean nativeMethodPrefixSupported;
    private final ClassVisitor delegate;

    public IronClassVisitor(String clazz, boolean nativeMethodPrefixSupported, ClassVisitor delegate)
    {
        assert clazz != null;
        assert delegate != null;

        this.clazz = clazz;
        this.nativeMethodPrefixSupported = nativeMethodPrefixSupported;
        this.delegate = delegate;
    }

    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) { delegate.visit(version, access, name, signature, superName, interfaces); }

    public void visitSource(String source, String debug) { delegate.visitSource(source, debug); }

    public void visitOuterClass(String owner, String name, String desc) { delegate.visitOuterClass(owner, name, desc); }

    public AnnotationVisitor visitAnnotation(String desc, boolean visible) { return delegate.visitAnnotation(desc, visible);}

    public void visitAttribute(Attribute attr) { delegate.visitAttribute(attr); }

    public void visitInnerClass(String name, String outerName, String innerName, int access) { delegate.visitInnerClass(name, outerName, innerName, access); }

    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) { return delegate.visitField(access, name, desc, signature, value); }

    public MethodVisitor visitMethod(int access, final String name, final String desc, String signature, String[] exceptions)
    {
        LOGGER.entering(CLASS_NAME, "visitMethod", new Object[]{access, name, desc, signature, exceptions});

        boolean isNative = (access & ACC_NATIVE) != 0;
        boolean isSynchronized = (access & ACC_SYNCHRONIZED) != 0;
        boolean isStatic = (access & ACC_STATIC) != 0;
        MethodVisitor mv;

        /**
         * If native method prefix is supported we inject our own wrapper method.
         */
        if (nativeMethodPrefixSupported && isNative)
        {
            delegate.visitMethod(access, IronAgent.NATIVE_METHOD_PREFIX + name, desc, signature, exceptions);

            mv = delegate.visitMethod(access ^ (ACC_NATIVE | ACC_SYNCHRONIZED), name, desc, signature, exceptions);
        }
        else
        {
            mv = delegate.visitMethod((isSynchronized ? access ^ ACC_SYNCHRONIZED : access), name, desc, signature, exceptions);
        }

        mv = new WaitNotifyMethodVisitor(mv);

        BeginEndMethodVisitor bemv = new BeginEndMethodVisitor(mv, access, name, desc, signature, exceptions);

        if (isSynchronized)
        {
            LOGGER.finest("Method is synchronized");

            final Type classType = Type.getType("L" + clazz.replaceAll("\\.", "/") + ";.class");
            if (isStatic)
            {
                LOGGER.finest("Method is static");
                bemv.getListeners().add(new BeginEndMethodListener()
                {
                    @Override
                    public void begin(MethodVisitor visitor)
                    {
                        push(visitor, line);
                        visitor.visitLdcInsn(classType);
                        visitor.visitMethodInsn(INVOKESTATIC, "com/toolazydogs/maiden/IronMaiden", "lockObject", "(ILjava/lang/Object;)V");
                    }

                    @Override
                    public void end(MethodVisitor visitor)
                    {
                        push(visitor, line);
                        visitor.visitLdcInsn(classType);
                        visitor.visitMethodInsn(INVOKESTATIC, "com/toolazydogs/maiden/IronMaiden", "unlockObject", "(ILjava/lang/Object;)V");
                    }
                });
            }
            else
            {
                LOGGER.finest("Method is not static");
                bemv.getListeners().add(new BeginEndMethodListener()
                {
                    @Override
                    public void begin(MethodVisitor visitor)
                    {
                        push(visitor, line);
                        visitor.visitVarInsn(ALOAD, 0);
                        visitor.visitMethodInsn(INVOKESTATIC, "com/toolazydogs/maiden/IronMaiden", "lockObject", "(ILjava/lang/Object;)V");
                    }

                    @Override
                    public void end(MethodVisitor visitor)
                    {
                        push(visitor, line);
                        visitor.visitVarInsn(ALOAD, 0);
                        visitor.visitMethodInsn(INVOKESTATIC, "com/toolazydogs/maiden/IronMaiden", "unlockObject", "(ILjava/lang/Object;)V");
                    }
                });
            }
        }

        bemv.getListeners().add(new BeginEndMethodListener()
        {
            @Override
            public void begin(MethodVisitor visitor)
            {
                visitor.visitLdcInsn(clazz);
                visitor.visitLdcInsn(name);
                visitor.visitLdcInsn(desc);
                visitor.visitMethodInsn(INVOKESTATIC, "com/toolazydogs/maiden/IronMaiden", "push", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");
            }

            @Override
            public void end(MethodVisitor visitor)
            {
                push(visitor, line);
                visitor.visitMethodInsn(INVOKESTATIC, "com/toolazydogs/maiden/IronMaiden", "pop", "(I)V");
            }
        });

        MethodVisitor result = new MonitorMethodVisitor(bemv);

        if (nativeMethodPrefixSupported && isNative)
        {
            MethodAdapter adapter = new MethodAdapter(result);
            adapter.visitCode();

            int args = 0;
            if (!isStatic) adapter.visitVarInsn(ALOAD, args++);
            for (Type param : Type.getArgumentTypes(desc))
            {
                adapter.visitVarInsn(param.getOpcode(ILOAD), args++);
            }
            adapter.visitMethodInsn((isStatic ? INVOKESTATIC : INVOKEVIRTUAL), clazz.replaceAll("\\.", "/"), name, desc);

            adapter.visitInsn(RETURN);
            adapter.visitEnd();

            result = null;
        }

        LOGGER.exiting(CLASS_NAME, "visitMethod", result);

        return result;
    }

    public void visitEnd()
    {
        delegate.visitEnd();
    }
}
