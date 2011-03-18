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
public class SynchronizedClassMethodVisitor extends DefaultMethodVisitor implements Opcodes
{
    private final static String CLASS_NAME = SynchronizedClassMethodVisitor.class.getName();
    private final static Logger LOGGER = Logger.getLogger(CLASS_NAME);

    public SynchronizedClassMethodVisitor(MarkableMethodVisitor visitor)
    {
        super(visitor);
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

                methodVisitor.visitIntInsn(ALOAD, 0);
                methodVisitor.visitMethodInsn(INVOKESTATIC, "com/toolazydogs/maiden/IronMaiden", "lockObject", "(Ljava/lang/Object;)V");

                methodVisitor.visitCode();

                LOGGER.exiting(CLASS_NAME, "visitCode.flush");
            }
        });

        LOGGER.exiting(CLASS_NAME, "visitCode");
    }

    @Override
    public void visitInsn(final int opcode)
    {
        getDelayed().push(new DelayedMethodVisitor()
        {
            public void flush(MethodVisitor methodVisitor, boolean mark)
            {
                LOGGER.entering(CLASS_NAME, "visitInsn.flush", new Object[]{methodVisitor, mark});

                switch (opcode)
                {
                    case IRETURN:
                    case LRETURN:
                    case FRETURN:
                    case DRETURN:
                    case ARETURN:
                    case RETURN:
                        methodVisitor.visitIntInsn(ALOAD, 0);
                        methodVisitor.visitMethodInsn(INVOKESTATIC, "com/toolazydogs/maiden/IronMaiden", "unlockObject", "(Ljava/lang/Object;)V");
                        break;

                    default:
                }

                methodVisitor.visitInsn(opcode);

                LOGGER.exiting(CLASS_NAME, "visitInsn.flush");
            }
        });
    }

    @Override
    public void visitVarInsn(final int opcode, final int var)
    {
        getDelayed().push(new DelayedMethodVisitor()
        {
            public void flush(MethodVisitor methodVisitor, boolean mark)
            {
                LOGGER.entering(CLASS_NAME, "visitVarInsn.flush", new Object[]{methodVisitor, mark});

                switch (opcode)
                {
                    case RET:
                        methodVisitor.visitIntInsn(ALOAD, 0);
                        methodVisitor.visitMethodInsn(INVOKESTATIC, "com/toolazydogs/maiden/IronMaiden", "unlockObject", "(Ljava/lang/Object;)V");
                        break;

                    default:
                }

                methodVisitor.visitVarInsn(opcode, var);

                LOGGER.exiting(CLASS_NAME, "visitVarInsn.flush");
            }
        });
    }
}
