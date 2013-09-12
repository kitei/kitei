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
package org.kitei.internal;

import static org.hamcrest.CoreMatchers.is;

import org.junit.Assert;
import org.junit.Test;


public class TestFormattingUtils
{
    @Test
    public void TestBadFormatting()
    {
        String result = FormattingUtils.safeFormat("Not a number: %d", "100");
        Assert.assertThat(result, is("'Not a number: %d', args: [100]\n(Caught IllegalFormatConversionException from String.format)"));
    }

    @Test
    public void testBadValue()
    {
        String result = FormattingUtils.safeFormat("Explodes: %s", new Exploder(null));
        Assert.assertThat(result, is("'Explodes: %s', args: [<NullPointerException>]\n(Caught NullPointerException from String.format)"));
    }


    private static class Exploder
    {
        private final String value;

        private Exploder(final String value)
        {
            this.value = value;
        }

        @Override
        public String toString()
        {
            return value.toString();
        }
    }
}
