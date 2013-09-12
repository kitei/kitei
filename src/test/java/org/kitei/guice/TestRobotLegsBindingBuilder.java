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
package org.kitei.guice;

import org.junit.Test;
import org.kitei.guice.robotlegs.Bar;
import org.kitei.guice.robotlegs.Baz;
import org.kitei.guice.robotlegs.Foo;
import org.kitei.guice.robotlegs.ForA;
import org.kitei.guice.robotlegs.ForB;
import org.kitei.guice.robotlegs.ImmutableFoo;
import org.kitei.guice.robotlegs.MutableFoo;

import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.Stage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.kitei.guice.RobotLegsBindingBuilder.bindRobotLegs;

public class TestRobotLegsBindingBuilder
{
    @Test
    public void testSimple()
    {
        Injector inj = Guice.createInjector(Stage.PRODUCTION,
                                            new Module() {
                                                @Override
                                                public void configure(final Binder binder) {
                                                    binder.bind(Foo.class).annotatedWith(ForA.class).toInstance(new ImmutableFoo("A"));
                                                    binder.bind(Foo.class).annotatedWith(ForB.class).toInstance(new ImmutableFoo("B"));
                                                    binder.bind(Baz.class).in(Scopes.SINGLETON);

                                                    bindRobotLegs(binder, Bar.class, ForA.class);
                                                    bindRobotLegs(binder, Bar.class, ForB.class);
                                                }
                                            });

        Bar barA = inj.getInstance(Key.get(Bar.class, ForA.class));
        Bar barB = inj.getInstance(Key.get(Bar.class, ForB.class));

        assertEquals("A", barA.getFoo().getValue());
        assertEquals("B", barB.getFoo().getValue());

        assertSame(barA.getBaz(), barB.getBaz());
    }

    @Test
    public void testMethodInjection()
    {
        Injector inj = Guice.createInjector(Stage.PRODUCTION,
                                            new Module() {
                                                @Override
                                                public void configure(final Binder binder) {
                                                    binder.bindConstant().annotatedWith(ForA.class).to("A");
                                                    binder.bindConstant().annotatedWith(ForB.class).to("B");

                                                    bindRobotLegs(binder, Foo.class, MutableFoo.class, ForA.class);
                                                    bindRobotLegs(binder, Foo.class, MutableFoo.class, ForB.class);

                                                    binder.bind(Baz.class).in(Scopes.SINGLETON);

                                                    bindRobotLegs(binder, Bar.class, ForA.class);
                                                    bindRobotLegs(binder, Bar.class, ForB.class);
                                                }
                                            });

        Foo fooA = inj.getInstance(Key.get(Foo.class, ForA.class));
        Foo fooB = inj.getInstance(Key.get(Foo.class, ForB.class));

        assertNotSame(fooA, fooB);

        assertEquals("A", fooA.getValue());
        assertEquals("B", fooB.getValue());

        Bar barA = inj.getInstance(Key.get(Bar.class, ForA.class));
        Bar barB = inj.getInstance(Key.get(Bar.class, ForB.class));

        assertEquals("A", barA.getFoo().getValue());
        assertEquals("B", barB.getFoo().getValue());

        assertSame(barA.getBaz(), barB.getBaz());
    }
}
