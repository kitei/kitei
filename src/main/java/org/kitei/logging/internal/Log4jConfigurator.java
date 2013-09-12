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
package org.kitei.logging.internal;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.helpers.Loader;
import org.apache.log4j.xml.DOMConfigurator;
import org.kitei.logging.Log;

import com.google.common.base.Preconditions;

/**
 * Configure logging using log4j 1.x.
 * <ul>
 * <li>Load /config/test-log4j.xml from classpath.</li>
 * <li>Load /config/log4j.xml from classpath.</li>
 * <li>Load /log4j.xml from classpath.</li>
 * </ul>
 * The preferred way of configuration is using the <tt>log4j.configuration</tt> system property. If this system property is set, do
 * nothing.
 *
 * @see LogManager
 */
public final class Log4jConfigurator
{
    private static final String[] DEFAULT_LOG_FILES = new String[] {
                    "/config/test-log4j.xml",
                    "/config/log4j.xml",
                    "/log4j.xml",
    };

    private Log4jConfigurator()
    {
    }

    public static URL configure()
    {
        final String log4jConfig = System.getProperty("log4j.configuration");

        if (log4jConfig != null) {
            // Log4j environment variable found, the LogManager will take care of
            // itself, don't bother reinitializing

            // This is what the LogManager does to find the resource.

            URL configUrl = null;
            try {
                configUrl = new URL(log4jConfig);
            }
            catch (MalformedURLException mue) {
                configUrl = Loader.getResource(log4jConfig);
            }

            Preconditions.checkState(configUrl != null, "Invalid config URL: %s", log4jConfig);

            return configUrl;
        }

        for (final String logFile : DEFAULT_LOG_FILES) {
            final URL configUrl = Log4jConfigurator.class.getResource(logFile);
            if (configUrl != null) {
                LogManager.resetConfiguration();
                DOMConfigurator.configure(configUrl);

                final Log log = Log.findLog();
                log.info("Configured logging from '%s'", configUrl);
                return configUrl;
            }
        }

        BasicConfigurator.configure();
        final Log log = Log.findLog();
        log.warn("No logging configuration, falling back to default!");

        return null;
    }
}
