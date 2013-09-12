/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kitei.guice.robotlegs;

import org.kitei.guice.RobotLeg;

import javax.inject.Inject;

public class Bar
{
    private final Foo foo;
    private final Baz baz;

    @Inject
    public Bar(@RobotLeg final Foo foo,
               final Baz baz)
    {
        this.foo = foo;
        this.baz = baz;
    }

    public Foo getFoo()
    {
        return foo;
    }

    public Baz getBaz()
    {
        return baz;
    }
}

