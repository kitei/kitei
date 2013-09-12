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

import org.junit.Assert;
import org.junit.Test;
import org.kitei.logging.internal.Log4jConfigurator;

public class TestLog4jConfigurator
{
    @Test
    public void configureBasicLogging()
    {
    	final URL url = Log4jConfigurator.configure();
        Assert.assertNull(url);
    }

    @Test
    public void configureLog4jLogging()
    {
        System.setProperty("log4j.configuration", "log4j-logging-test.xml");
    	final URL url = Log4jConfigurator.configure();

        Assert.assertTrue(url.toString().startsWith("file:/"));
        Assert.assertTrue(url.toString().endsWith("/log4j-logging-test.xml"));
    }
}
