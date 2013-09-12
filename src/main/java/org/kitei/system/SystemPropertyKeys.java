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
package org.kitei.system;

/**
 * Contains the names of all well known system properties in the kitei framework.
 *
 * All names start with <tt>kitei.</tt>.
 * Second component is the subsystem for which the property is relevant.
 */
public final class SystemPropertyKeys
{
    private SystemPropertyKeys()
    {
    }

    /**
     * Controls logging of exceptions in INFO, WARN and ERROR levels.
     *
     * If this system property is set to <tt>true</tt>, exceptions logged at INFO, WARN and ERROR
     * level will not log their stacktrace, unless the category they log to is actually at DEBUG level.
     */
    public static final String LOGGING_TRIM_EXCEPTIONS = "kitei.logging.trim-exceptions";
}


