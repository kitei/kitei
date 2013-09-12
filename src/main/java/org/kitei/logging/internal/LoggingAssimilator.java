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

import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.kitei.logging.Log;
import org.slf4j.bridge.SLF4JBridgeHandler;

/**
 * Assimilates other logging frameworks and redirect to slf4j.
 */
public final class LoggingAssimilator
{
    private static final Log LOG = Log.findLog();

    private LoggingAssimilator()
    {
    }

    public synchronized static void assimilateJavaUtilLogging()
    {
        final Logger rootLogger = LogManager.getLogManager().getLogger("");
        final Handler[] handlers = rootLogger.getHandlers();

        if (handlers != null) {
            for (Handler handler : handlers) {
                rootLogger.removeHandler(handler);
            }
        }

        SLF4JBridgeHandler.install();

        LOG.info("java.util.logging redirected to slf4j.");
    }

    /**
     * Try to unassimilate the logging frameworks.
     */
    public synchronized static void unassimilateJavaUtilLogging()
    {
        SLF4JBridgeHandler.uninstall();
    }
}
