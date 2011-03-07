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

import java.util.Stack;
import java.util.logging.Logger;

import org.objectweb.asm.AnnotationVisitor;


/**
 *
 */
public class InnerAnnotationVisitor implements DelayedAnnotationVisitor, AnnotationVisitor
{
    private final static String CLASS_NAME = InnerAnnotationVisitor.class.getName();
    private final static Logger LOGGER = Logger.getLogger(CLASS_NAME);
    private final Stack<DelayedAnnotationVisitor> delayed = new Stack<DelayedAnnotationVisitor>();
    private final DelayedAnnotationVisitor flush;

    public InnerAnnotationVisitor(final String name, final String desc)
    {
        flush = new DelayedAnnotationVisitor()
        {
            public void flush(AnnotationVisitor annotationVisitor, boolean mark)
            {
                doFlush(annotationVisitor.visitAnnotation(name, desc), mark);
            }
        };
    }

    public InnerAnnotationVisitor(final String name)
    {
        flush = new DelayedAnnotationVisitor()
        {
            public void flush(AnnotationVisitor annotationVisitor, boolean mark)
            {
                doFlush(annotationVisitor.visitArray(name), mark);
            }
        };
    }

    public void visit(final String name, final Object value)
    {
        delayed.push(new DelayedAnnotationVisitor()
        {
            public void flush(AnnotationVisitor annotationVisitor, boolean mark)
            {
                annotationVisitor.visit(name, value);
            }
        });
    }

    public void visitEnum(final String name, final String desc, final String value)
    {
        delayed.push(new DelayedAnnotationVisitor()
        {
            public void flush(AnnotationVisitor annotationVisitor, boolean mark)
            {
                annotationVisitor.visitEnum(name, desc, value);
            }
        });
    }

    public AnnotationVisitor visitAnnotation(String name, String desc)
    {
        return (AnnotationVisitor)delayed.push(new InnerAnnotationVisitor(name, desc));
    }

    public AnnotationVisitor visitArray(String name)
    {
        return (AnnotationVisitor)delayed.push(new InnerAnnotationVisitor(name));
    }

    public void visitEnd()
    {
        delayed.push(new DelayedAnnotationVisitor()
        {
            public void flush(AnnotationVisitor annotationVisitor, boolean mark)
            {
                annotationVisitor.visitEnd();
            }
        });
    }

    public void flush(AnnotationVisitor annotationVisitor, boolean mark)
    {
        flush.flush(annotationVisitor, mark);

    }

    private void doFlush(AnnotationVisitor visitor, boolean mark)
    {
        LOGGER.entering(CLASS_NAME, "flush", new Object[]{visitor, mark});

        for (DelayedAnnotationVisitor dav : delayed) dav.flush(visitor, mark);
        delayed.clear();

        LOGGER.exiting(CLASS_NAME, "flush");
    }
}
