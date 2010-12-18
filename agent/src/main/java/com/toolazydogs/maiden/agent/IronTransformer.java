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

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.logging.Logger;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;


/**
 * A class transformer for the iron maiden agent.
 */
public class IronTransformer implements ClassFileTransformer, Opcodes
{
    private final static String CLASS_NAME = IronTransformer.class.getName();
    private final static Logger LOGGER = Logger.getLogger(CLASS_NAME);

    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException
    {
        LOGGER.entering(CLASS_NAME, "transform", new Object[]{loader, className, classBeingRedefined, protectionDomain, classfileBuffer});

        ClassReader reader = new ClassReader(classfileBuffer);
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES & ClassWriter.COMPUTE_MAXS);
        reader.accept(writer, ClassReader.EXPAND_FRAMES);

        System.out.println("FOO " + className);

        LOGGER.exiting(CLASS_NAME, "transform", classfileBuffer);

        return writer.toByteArray();
    }
}
