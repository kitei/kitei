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

import org.apache.log4j.Level;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kitei.logging.util.LoggingTestUtils;
import org.kitei.logging.util.RecordingAppender;
import org.kitei.system.SystemPropertyKeys;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;


public class TestWarnDebugLogging
{
    private RecordingAppender recordingAppender = null;
    private Log log = null;

    @Before
    public void setUp()
    {
        recordingAppender = LoggingTestUtils.setupTestLogging("/log4j-logging-test.xml");
        System.setProperty(SystemPropertyKeys.LOGGING_TRIM_EXCEPTIONS, "true");
        log = Log.forCategory("tc-warn");
    }

    @After
    public void tearDown()
    {
        recordingAppender = null;
        log = null;
    }

    @Test
    public void testDebug()
    {
        final String format = "Format an int: %d and a String: '%s'";
        final int p1 = 23;
        final String p2 = "previously, on Lost";

        final Exception e = new IllegalArgumentException("wrong! do it again!");
        e.fillInStackTrace();

        final String resultShort = String.format(format, p1, p2) + ": " + e.getMessage();
        final String resultLong = String.format(format, p1, p2);

        log.warn(e, format, p1, p2);
        Assert.assertThat(recordingAppender.getContents(), is(resultShort + "\n"));
        Assert.assertThat(recordingAppender.getLevel(), is(Level.WARN));
        Assert.assertThat(recordingAppender.getThrowable(), is(nullValue()));

        recordingAppender.clear();

        Log log2 = Log.forCategory("tc-debug");

        log2.warn(e, format, p1, p2);
        Assert.assertThat(recordingAppender.getContents(), is(resultLong + "\n"));
        Assert.assertThat(recordingAppender.getLevel(), is(Level.WARN));
        Assert.assertThat(recordingAppender.getThrowable(), is(e.toString()));
    }

    @Test
    public void testDebug2()
    {
        final Exception e = new IllegalArgumentException("wrong! do it again!");
        e.fillInStackTrace();

        final String resultLong = "Hello, World";
        final String resultShort = resultLong + ": " + e.getMessage();

        log.warn(e, resultLong);
        Assert.assertThat(recordingAppender.getContents(), is(resultShort + "\n"));
        Assert.assertThat(recordingAppender.getLevel(), is(Level.WARN));
        Assert.assertThat(recordingAppender.getThrowable(), is(nullValue()));

        recordingAppender.clear();

        Log log2 = Log.forCategory("tc-debug");

        log2.warn(e, resultLong);
        Assert.assertThat(recordingAppender.getContents(), is(resultLong + "\n"));
        Assert.assertThat(recordingAppender.getLevel(), is(Level.WARN));
        Assert.assertThat(recordingAppender.getThrowable(), is(e.toString()));
    }

    @Test
    public void testDebug3()
    {
        final Exception e = new IllegalArgumentException("wrong! do it again!");
        e.fillInStackTrace();

        log.warn(e);
        Assert.assertThat(recordingAppender.getContents(), is(": " + e.getMessage() + "\n"));
        Assert.assertThat(recordingAppender.getLevel(), is(Level.WARN));
        Assert.assertThat(recordingAppender.getThrowable(), is(nullValue()));

        recordingAppender.clear();

        Log log2 = Log.forCategory("tc-debug");

        log2.warn(e);
        Assert.assertThat(recordingAppender.getContents(), is("\n"));
        Assert.assertThat(recordingAppender.getLevel(), is(Level.WARN));
        Assert.assertThat(recordingAppender.getThrowable(), is(e.toString()));
    }
}

