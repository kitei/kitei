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
package org.kitei.logging;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kitei.logging.util.LoggingTestUtils;
import org.kitei.logging.util.RecordingAppender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.is;


public class TestLog
{
    private Logger log = null;

    private RecordingAppender recordingAppender = null;

    @Before
    public void setUp()
    {
        recordingAppender = LoggingTestUtils.setupTestLogging("/log4j-logging-test.xml");
        log = LoggerFactory.getLogger(TestLog.class);
    }

    @After
    public void tearDown()
    {
        log = null;
    }

    @Test
    public void findLogger()
    {
        final Logger log2 = Log.findLog().getWrappedLogger();

        Assert.assertThat(log, is(log2));
    }

    @Test
    public void findLoggerByName()
    {
        final Logger log2 = Log.forCategory(this.getClass().getName()).getWrappedLogger();

        Assert.assertThat(log, is(log2));
    }

    @Test
    public void findLoggerByClass()
    {
        final Logger log2 = Log.forClass(this.getClass()).getWrappedLogger();

        Assert.assertThat(log, is(log2));
    }

    @Test
    public void findLoggerByLogger()
    {
        final Logger log2 = Log.forLogger(log).getWrappedLogger();

        Assert.assertThat(log, is(log2));
    }

    @Test
    public void testLevelEnabled()
    {
        final Log debugLogger = Log.forCategory("tc-debug");
        Assert.assertThat(debugLogger.isDebugEnabled(), is(true));
        Assert.assertThat(debugLogger.isInfoEnabled(), is(true));
        Assert.assertThat(debugLogger.isWarnEnabled(), is(true));
        Assert.assertThat(debugLogger.isErrorEnabled(), is(true));

        final Log infoLogger = Log.forCategory("tc-info");
        Assert.assertThat(infoLogger.isDebugEnabled(), is(false));
        Assert.assertThat(infoLogger.isInfoEnabled(), is(true));
        Assert.assertThat(infoLogger.isWarnEnabled(), is(true));
        Assert.assertThat(infoLogger.isErrorEnabled(), is(true));

        final Log warnLogger = Log.forCategory("tc-warn");
        Assert.assertThat(warnLogger.isDebugEnabled(), is(false));
        Assert.assertThat(warnLogger.isInfoEnabled(), is(false));
        Assert.assertThat(warnLogger.isWarnEnabled(), is(true));
        Assert.assertThat(warnLogger.isErrorEnabled(), is(true));

        final Log errorLogger = Log.forCategory("tc-error");
        Assert.assertThat(errorLogger.isDebugEnabled(), is(false));
        Assert.assertThat(errorLogger.isInfoEnabled(), is(false));
        Assert.assertThat(errorLogger.isWarnEnabled(), is(false));
        Assert.assertThat(errorLogger.isErrorEnabled(), is(true));
    }

    @Test
    public void TestBadFormatting()
    {
        Log warnLogger = Log.forCategory("tc-warn");

        warnLogger.warn("Not a number: %d", "100");

        Assert.assertThat(recordingAppender.getContents(), is("Invalid format string while logging: WARN 'Not a number: %d' [100]\n'Not a number: %d' [100]\n"));
    }
}
