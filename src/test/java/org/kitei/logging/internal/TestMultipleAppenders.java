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


import java.net.URL;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kitei.logging.Log;
import org.kitei.logging.internal.Log4jConfigurator;
import org.kitei.logging.util.RecordingAppender;

public class TestMultipleAppenders
{
    private final RecordingAppender r1 = new RecordingAppender();
    private final RecordingAppender r2 = new RecordingAppender();

    @Before
    public void setUp()
    {
        System.setProperty("log4j.configuration", "log4j-multi-test.xml");
    	final URL url = Log4jConfigurator.configure();

    	LogManager.getRootLogger().addAppender(r1);
    	LogManager.getLogger("secondary").addAppender(r2);

        Assert.assertTrue(url.toString().startsWith("file:/"));
        Assert.assertTrue(url.toString().endsWith("/log4j-multi-test.xml"));
    }

    @Test
    public void testSecondaryLog()
    {
        Assert.assertEquals("", r1.getContents());
        Assert.assertEquals("", r2.getContents());
        final Log log = Log.forCategory("secondary");
        log.info("Hello, World!");
        Assert.assertEquals("", r1.getContents());
        Assert.assertEquals("Hello, World!\n", r2.getContents());
    }

    @Test
    public void testRootLog()
    {
        Assert.assertEquals("", r1.getContents());
        Assert.assertEquals("", r2.getContents());
        final Logger logger = LogManager.getRootLogger();
        logger.info("Hello, World!");
        Assert.assertEquals("Hello, World!\n", r1.getContents());
        Assert.assertEquals("", r2.getContents());
    }

    @Test
    public void testRandomlog()
    {
        Assert.assertEquals("", r1.getContents());
        Assert.assertEquals("", r2.getContents());
        final Log log = Log.forCategory("some.random.string.that.does.not.match.any.logger");
        log.info("Hello, World!");
        Assert.assertEquals("Hello, World!\n", r1.getContents());
        Assert.assertEquals("", r2.getContents());
    }
}

