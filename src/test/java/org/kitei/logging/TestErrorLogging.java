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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;

import org.apache.log4j.Level;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kitei.logging.util.LoggingTestUtils;
import org.kitei.logging.util.RecordingAppender;
import org.kitei.system.SystemPropertyKeys;


public class TestErrorLogging
{
    private RecordingAppender recordingAppender = null;
    private Log log = null;

    @Before
    public void setUp()
    {
        recordingAppender = LoggingTestUtils.setupTestLogging("/log4j-logging-test.xml");
        System.setProperty(SystemPropertyKeys.LOGGING_TRIM_EXCEPTIONS, "false");
        log = Log.forCategory("tc-error");
    }

    @After
    public void tearDown()
    {
        recordingAppender = null;
        log = null;
    }

    @Test
    public void testSimple()
    {
        final String msg = "Hello";

        log.trace(msg);
        Assert.assertThat(recordingAppender.getContents(), is(""));
        Assert.assertThat(recordingAppender.getLevel(), is(nullValue()));
        Assert.assertThat(recordingAppender.getThrowable(), is(nullValue()));

        recordingAppender.clear();

        log.debug(msg);
        Assert.assertThat(recordingAppender.getContents(), is(""));
        Assert.assertThat(recordingAppender.getLevel(), is(nullValue()));
        Assert.assertThat(recordingAppender.getThrowable(), is(nullValue()));

        recordingAppender.clear();

        log.info(msg);
        Assert.assertThat(recordingAppender.getContents(), is(""));
        Assert.assertThat(recordingAppender.getLevel(), is(nullValue()));
        Assert.assertThat(recordingAppender.getThrowable(), is(nullValue()));

        recordingAppender.clear();

        log.warn(msg);
        Assert.assertThat(recordingAppender.getContents(), is(""));
        Assert.assertThat(recordingAppender.getLevel(), is(nullValue()));
        Assert.assertThat(recordingAppender.getThrowable(), is(nullValue()));

        recordingAppender.clear();

        log.error(msg);
        Assert.assertThat(recordingAppender.getContents(), is(msg + "\n"));
        Assert.assertThat(recordingAppender.getLevel(), is(Level.ERROR));
        Assert.assertThat(recordingAppender.getThrowable(), is(nullValue()));
    }

    @Test
    public void testStringFormat()
    {
        final String format = "Format an int: %d and a String: '%s'";
        final int p1 = 23;
        final String p2 = "previously, on Lost";
        final String result = String.format(format, p1, p2);

        log.trace(format, p1, p2);
        Assert.assertThat(recordingAppender.getContents(), is(""));
        Assert.assertThat(recordingAppender.getLevel(), is(nullValue()));
        Assert.assertThat(recordingAppender.getThrowable(), is(nullValue()));

        recordingAppender.clear();

        log.debug(format, p1, p2);
        Assert.assertThat(recordingAppender.getContents(), is(""));
        Assert.assertThat(recordingAppender.getLevel(), is(nullValue()));
        Assert.assertThat(recordingAppender.getThrowable(), is(nullValue()));

        recordingAppender.clear();

        log.info(format, p1, p2);
        Assert.assertThat(recordingAppender.getContents(), is(""));
        Assert.assertThat(recordingAppender.getLevel(), is(nullValue()));
        Assert.assertThat(recordingAppender.getThrowable(), is(nullValue()));

        recordingAppender.clear();

        log.warn(format, p1, p2);
        Assert.assertThat(recordingAppender.getContents(), is(""));
        Assert.assertThat(recordingAppender.getLevel(), is(nullValue()));
        Assert.assertThat(recordingAppender.getThrowable(), is(nullValue()));

        recordingAppender.clear();

        log.error(format, p1, p2);
        Assert.assertThat(recordingAppender.getContents(), is(result + "\n"));
        Assert.assertThat(recordingAppender.getLevel(), is(Level.ERROR));
        Assert.assertThat(recordingAppender.getThrowable(), is(nullValue()));
    }

    @Test
    public void testThrowable()
    {
        final String format = "Format an int: %d and a String: '%s'";
        final int p1 = 23;
        final String p2 = "previously, on Lost";

        final Exception e = new IllegalArgumentException("wrong! do it again!");
        e.fillInStackTrace();

        final String result = String.format(format, p1, p2);

        log.trace(e, format, p1, p2);
        Assert.assertThat(recordingAppender.getContents(), is(""));
        Assert.assertThat(recordingAppender.getLevel(), is(nullValue()));
        Assert.assertThat(recordingAppender.getThrowable(), is(nullValue()));

        recordingAppender.clear();

        log.debug(e, format, p1, p2);
        Assert.assertThat(recordingAppender.getContents(), is(""));
        Assert.assertThat(recordingAppender.getLevel(), is(nullValue()));
        Assert.assertThat(recordingAppender.getThrowable(), is(nullValue()));

        recordingAppender.clear();

        log.info(e, format, p1, p2);
        Assert.assertThat(recordingAppender.getContents(), is(""));
        Assert.assertThat(recordingAppender.getLevel(), is(nullValue()));
        Assert.assertThat(recordingAppender.getThrowable(), is(nullValue()));

        recordingAppender.clear();

        log.warn(e, format, p1, p2);
        Assert.assertThat(recordingAppender.getContents(), is(""));
        Assert.assertThat(recordingAppender.getLevel(), is(nullValue()));
        Assert.assertThat(recordingAppender.getThrowable(), is(nullValue()));

        recordingAppender.clear();

        log.error(e, format, p1, p2);
        Assert.assertThat(recordingAppender.getContents(), is(result + "\n"));
        Assert.assertThat(recordingAppender.getLevel(), is(Level.ERROR));
        Assert.assertThat(recordingAppender.getThrowable(), is(e.toString()));
    }
}

