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

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Binder;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.ProvisionException;
import com.google.inject.Scope;
import com.google.inject.binder.ScopedBindingBuilder;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.InjectionPoint;
import com.google.inject.spi.ProviderWithDependencies;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A binder to solve the "Robot legs" binding problem. The Robot legs problem is when binding an instance using an annotation, having the resulting object get its dependencies looked up with
 * the same annotation.
 *
 *  By using a "placeholder annotation" {@link RobotLeg} which gets dynamically resolved to the actual binding, this provider can resolve dependencies correctly.
 */
public class RobotLegsBindingBuilder<T>
    implements ScopedBindingBuilder
{
    public static <T> RobotLegsBindingBuilder<T> bindRobotLegs(final Binder binder,
                                                               final Class<T> clazz,
                                                               final Class<? extends Annotation> annotation)
    {
        return bindRobotLegs(binder, clazz, clazz, annotation);
    }

    public static <T> RobotLegsBindingBuilder<T> bindRobotLegs(final Binder binder,
                                                               final Class<T> clazz,
                                                               final Class<? extends T> implementationClazz,
                                                               final Class<? extends Annotation> annotation)
    {
        final RobotLegsProvider<T> provider = new RobotLegsProvider<T>(implementationClazz, annotation);
        return new RobotLegsBindingBuilder<T>(provider, binder.bind(clazz).annotatedWith(annotation).toProvider(provider));
    }


    private final RobotLegsProvider<T> delegate;
    private final ScopedBindingBuilder scopedBindingBuilder;

    public RobotLegsBindingBuilder(final RobotLegsProvider<T> delegate,
                                   final ScopedBindingBuilder scopedBindingBuilder)
    {
        this.delegate = Preconditions.checkNotNull(delegate, "delegate");
        this.scopedBindingBuilder = Preconditions.checkNotNull(scopedBindingBuilder, "scopedBindingBuilder");
    }

    @Override
    public void in(final Class<? extends Annotation> scopeAnnotation)
    {
        scopedBindingBuilder.in(scopeAnnotation);
    }

    @Override
    public void in(final Scope scope)
    {
        scopedBindingBuilder.in(scope);
    }

    @Override
    public void asEagerSingleton()
    {
        scopedBindingBuilder.asEagerSingleton();
    }

    static class RobotLegsProvider<T> implements ProviderWithDependencies<T>
    {
        private final Class<? extends T> clazz;
        private final Map<Dependency<?>, Dependency<?>> dependencies;

        private Injector robotLegsInjector;

        RobotLegsProvider(final Class<? extends T> clazz, final Class<? extends Annotation> annotation)
        {
            checkNotNull(annotation, "annotation is null");
            this.clazz = checkNotNull(clazz, "clazz is null");

            ImmutableSet.Builder<InjectionPoint> injectionPointBuilder = ImmutableSet.builder();
            injectionPointBuilder.add(InjectionPoint.forConstructorOf(clazz));
            injectionPointBuilder.addAll(InjectionPoint.forInstanceMethodsAndFields(clazz));

            ImmutableMap.Builder<Dependency<?>, Dependency<?>> builder = ImmutableMap.builder();
            for (InjectionPoint injectionPoint : injectionPointBuilder.build()) {
                for (Dependency<?> dependency : injectionPoint.getDependencies()) {
                    final Annotation dependencyAnnotation = dependency.getKey().getAnnotation();
                    if (dependencyAnnotation != null && dependencyAnnotation.annotationType() == RobotLeg.class) {
                        Dependency<?> annotatedDependency = Dependency.get(Key.get(dependency.getKey().getTypeLiteral(), annotation));
                        builder.put(annotatedDependency, dependency);
                    }
                }
            }

            this.dependencies = builder.build();
        }

        @Inject
        void setInjector(final Injector injector)
        {
            this.robotLegsInjector = injector.createChildInjector(new Module() {
                @SuppressWarnings("unchecked")
                @Override
                public void configure(final Binder binder)
                {
                    binder.bind(clazz);

                    for (Map.Entry<Dependency<?>, Dependency<?>> entry : dependencies.entrySet()) {
                        if (injector.getExistingBinding(entry.getKey().getKey()) != null) {
                            Key<Object> destKey = ((Dependency<Object>) entry.getValue()).getKey();
                            Key<Object> srcKey = ((Dependency<Object>) entry.getKey()).getKey();
                            binder.bind(destKey).to(srcKey);
                        }
                        else {
                            throw new ProvisionException("No binding for " + entry.getKey().getKey() + " available.");
                        }
                    }

                }
            });
        }

        @Override
        public T get()
        {
            return robotLegsInjector.getInstance(clazz);
        }

        @Override
        public Set<Dependency<?>> getDependencies()
        {
            return dependencies.keySet();
        }
    }
}
