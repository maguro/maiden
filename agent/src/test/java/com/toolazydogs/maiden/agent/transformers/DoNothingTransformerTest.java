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
package com.toolazydogs.maiden.agent.transformers;

import java.lang.instrument.IllegalClassFormatException;

import static org.testng.Assert.assertSame;
import org.testng.annotations.Test;


/**
 *
 */
public class DoNothingTransformerTest
{
    @Test
    public void test() throws IllegalClassFormatException
    {
        byte[] mock = new byte[1];

        assertSame(null, new DoNothingTransformer().transform(null, null, null, null, mock));
        assertSame(null, new DoNothingTransformer(null).transform(null, null, null, null, mock));
    }
}
