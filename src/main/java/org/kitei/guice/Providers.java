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
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.InjectionPoint;
import com.google.inject.spi.ProviderWithDependencies;

import java.util.Set;

/**
 * Helpers for {@link com.google.inject.Provider}.
 */
public class Providers
{
    private Providers()
    {
    }

    /**
     * Return a @{link Provider} for a {@link Supplier}.
     */
    public static <T> Provider<T> guicify(final Supplier<T> delegate)
    {
        Preconditions.checkNotNull(delegate, "delegate");

        final Set<InjectionPoint> injectionPoints = InjectionPoint.forInstanceMethodsAndFields(delegate.getClass());
        if (injectionPoints.isEmpty()) {
            return new Provider<T>() {
                @Override
                public T get()
                {
                    return delegate.get();
                }

                @Override
                public String toString()
                {
                    return "guicified(" + delegate + ")";
                }
            };
        }
        else {
            final ImmutableSet.Builder<Dependency<?>> builder = ImmutableSet.builder();

            for (final InjectionPoint ip : injectionPoints) {
                builder.addAll(ip.getDependencies());
            }

            final Set<Dependency<?>> dependencies = builder.build();
            return new ProviderWithDependencies<T>() {
                @Inject
                void initialize(final Injector injector)
                {
                    injector.injectMembers(delegate);
                }

                public Set<Dependency<?>> getDependencies()
                {
                    return dependencies;
                }

                public T get()
                {
                    return delegate.get();
                }

                @Override
                public String toString()
                {
                    return "guicified(" + delegate + ")";
                }
            };
        }
    }
}
