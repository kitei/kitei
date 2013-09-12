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
package org.kitei.logging.util;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URL;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

public final class LoggingTestUtils
{
    private LoggingTestUtils()
    {
    }

    public static final RecordingAppender setupTestLogging(final String logName)
    {
        final URL log4jFile = checkNotNull(LoggingTestUtils.class.getResource(logName), "no log4j file found");
        DOMConfigurator.configure(log4jFile);

        final Logger rootLogger = LogManager.getRootLogger();
        final RecordingAppender recordingAppender = new RecordingAppender();

        rootLogger.addAppender(recordingAppender);

        return recordingAppender;
    }

    public static final RecordingAppender addAppender(final String category)
    {
        final Logger logger = LogManager.getLogger(category);
        final RecordingAppender recordingAppender = new RecordingAppender();

        logger.addAppender(recordingAppender);

        return recordingAppender;
    }
}
