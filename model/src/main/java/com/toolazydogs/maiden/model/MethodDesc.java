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
public class MethodDesc
{
    private final String classname;
    private final String name;
    private final String desc;

    public MethodDesc(String classname, String name, String desc)
    {
        assert classname != null;
        assert name != null;
        assert desc != null;

        this.classname = classname;
        this.name = name;
        this.desc = desc;
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

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MethodDesc that = (MethodDesc)o;

        return classname.equals(that.classname) && desc.equals(that.desc) && name.equals(that.name);
    }

    @Override
    public int hashCode()
    {
        int result = classname.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + desc.hashCode();
        return result;
    }
}
