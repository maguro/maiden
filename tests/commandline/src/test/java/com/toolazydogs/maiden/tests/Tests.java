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
package com.toolazydogs.maiden.tests;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintWriter;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.TraceClassVisitor;
import org.testng.annotations.Test;

import com.toolazydogs.maiden.agent.asm.IronClassVisitor;


/**
 *
 */
public class Tests
{
    @Test
    public void t() throws Exception
    {
        print("target/test-classes/com/acme/Pojo.class", "com.acme.Pojo");
        print("target/test-classes/com/acme/Pojo$1.class", "com.acme.Pojo$1");
    }

    private static void print(String filename, String clazz) throws Exception
    {
        InputStream in = new FileInputStream(filename);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;

        while ((len = in.read(buffer)) != -1) out.write(buffer, 0, len);

        in.close();
        out.close();

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);

        ClassReader reader = new ClassReader(out.toByteArray());
        reader.accept(new IronClassVisitor(clazz, true, writer), ClassReader.EXPAND_FRAMES);

        reader = new ClassReader(writer.toByteArray());

        writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        reader.accept(new CheckClassAdapter(writer), ClassReader.EXPAND_FRAMES);

        reader = new ClassReader(writer.toByteArray());
        reader.accept(new TraceClassVisitor(null, new PrintWriter(System.out)), ClassReader.EXPAND_FRAMES);
    }
}
