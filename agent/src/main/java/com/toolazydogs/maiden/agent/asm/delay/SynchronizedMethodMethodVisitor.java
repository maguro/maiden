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

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;


/**
 *
 */
public class SynchronizedMethodMethodVisitor extends DefaultMethodVisitor
{
    private final static String CLASS_NAME = SynchronizedMethodMethodVisitor.class.getName();
    private final static Logger LOGGER = Logger.getLogger(CLASS_NAME);
    private final String clazz;
    private final String name;
    private final String desc;

    public SynchronizedMethodMethodVisitor(MarkableMethodVisitor visitor, String clazz, String name, String desc)
    {
        super(visitor);

        assert clazz != null;
        assert name != null;
        assert desc != null;

        this.clazz = clazz;
        this.name = name;
        this.desc = desc;
    }

    @Override
    public void visitCode()
    {
        LOGGER.entering(CLASS_NAME, "visitCode");

        getDelayed().push(new DelayedMethodVisitor()
        {
            public void flush(MethodVisitor methodVisitor, boolean mark)
            {
                LOGGER.entering(CLASS_NAME, "visitCode.flush", new Object[]{methodVisitor, mark});

                methodVisitor.visitCode();

                if (mark)
                {
                    methodVisitor.visitLdcInsn(clazz);
                    methodVisitor.visitLdcInsn(name);
                    methodVisitor.visitLdcInsn(desc);
                    methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, "com/toolazydogs/maiden/IronMaiden", "announceMethod", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");
                }

                LOGGER.exiting(CLASS_NAME, "visitCode.flush");
            }
        });

        LOGGER.exiting(CLASS_NAME, "visitCode");
    }
}
