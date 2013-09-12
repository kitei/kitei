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

import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;

import javax.annotation.Nullable;

/**
 * Various helpers for Google Guice.
 */
public final class GuiceUtils
{
    private GuiceUtils()
    {
    }

    /**
     * Returns a binding from an injector or a default value if no binding exists.
     */
    public static <T> T getInstanceWithDefault(final Injector injector, final Key<T> key, @Nullable final T defaultValue)
    {
        final Binding<T> binding = injector.getExistingBinding(key);
        return (binding == null) ? defaultValue : injector.getInstance(key);
    }

    /**
     * Returns a binding from an injector or a default value if no binding exists.
     */
    public static <T> T getInstanceWithDefault(final Injector injector, final Class<T> clazz, @Nullable final T defaultValue)
    {
        final Binding<T> binding = injector.getExistingBinding(Key.get(clazz));
        return (binding == null) ? defaultValue : injector.getInstance(clazz);
    }
}
