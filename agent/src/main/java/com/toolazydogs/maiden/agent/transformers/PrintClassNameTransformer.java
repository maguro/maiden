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
package com.toolazydogs.maiden.agent.transformers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Properties;
import java.util.logging.Logger;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import com.toolazydogs.maiden.agent.asm.IronClassVisitor;


/**
 *
 */
public class PrintClassNameTransformer implements ClassFileTransformer
{
    private final static String CLASS_NAME = PrintClassNameTransformer.class.getName();
    private final static Logger LOGGER = Logger.getLogger(CLASS_NAME);

    public PrintClassNameTransformer(Properties properties)
    {
    }

    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException
    {
        LOGGER.entering(CLASS_NAME, "transform", new Object[]{loader, className, classBeingRedefined, protectionDomain, classfileBuffer});

        ClassReader reader = new ClassReader(classfileBuffer);
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        reader.accept(new IronClassVisitor(className, writer), ClassReader.EXPAND_FRAMES);

        byte[] result = writer.toByteArray();

        if ("org/apache/maven/surefire/report/XMLReporter".equals(className))
        {
            try
            {
                File file = new File("/Users/acabrera/XMLReporter.class");
                OutputStream out = new FileOutputStream(file);
                InputStream in = new ByteArrayInputStream(result);
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = in.read(buffer)) != -1) out.write(buffer, 0, len);
                out.close();
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();  //Todo change body of catch statement use File | Settings | File Templates.
            }
            catch (IOException e)
            {
                e.printStackTrace();  //Todo change body of catch statement use File | Settings | File Templates.
            }
        }

        LOGGER.exiting(CLASS_NAME, "transform", result);

        return result;
    }
}
