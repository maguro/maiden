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
public class Line
{
    private final String classname;
    private final String name;
    private final String desc;
    private final int line;

    public Line(String classname, String name, String desc, int line)
    {
        assert classname != null;
        assert name != null;
        assert desc != null;

        this.classname = classname;
        this.name = name;
        this.desc = desc;
        this.line = line;
    }

    public String getClassname()
    {
        return classname;
    }

    public String getName()
    {
        return name;
    }

    public String getDesc()
    {
        return desc;
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

        Line line1 = (Line)o;

        if (line != line1.line) return false;
        if (!classname.equals(line1.classname)) return false;
        if (!desc.equals(line1.desc)) return false;
        return name.equals(line1.name);
    }

    @Override
    public int hashCode()
    {
        int result = classname.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + desc.hashCode();
        result = 31 * result + line;
        return result;
    }

    @Override
    public String toString()
    {
        return classname + '.' + name + ' ' + desc + ": " + line;
    }
}
