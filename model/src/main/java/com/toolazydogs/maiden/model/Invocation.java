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
package com.toolazydogs.maiden.model;

/**
 *
 */
public class Invocation
{
    private final MethodDesc methodDesc;
    private final int line;

    public Invocation(MethodDesc methodDesc, int line)
    {
        assert methodDesc != null;

        this.methodDesc = methodDesc;
        this.line = line;
    }

    public String getClassname()
    {
        return methodDesc.getClassname();
    }

    public String getName()
    {
        return methodDesc.getName();
    }

    public String getDesc()
    {
        return methodDesc.getDesc();
    }

    public int getLine()
    {
        return line;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Invocation that = (Invocation)o;

        return line == that.line && methodDesc.equals(that.methodDesc);
    }

    @Override
    public int hashCode()
    {
        int result = methodDesc.hashCode();
        result = 31 * result + line;
        return result;
    }
}
