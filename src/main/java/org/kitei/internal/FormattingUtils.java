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

import static java.lang.String.format;

import java.util.Arrays;
import java.util.Iterator;

public final class FormattingUtils
{
    private FormattingUtils()
    {
    }

    public static String safeFormat(final String msg, final Object... args)
    {
        if (msg == null) {
            return args.length == 0 ? "" : safeArgs(args);
        }
        else {
            try {
                return format(msg, args);
            }
            catch (Exception ife) {
                return "'" + msg + "', args: " + safeArgs(args) + "\n(Caught " + ife.getClass().getSimpleName() + " from String.format)";
            }
        }
    }

    public static String safeArgs(Object ... args)
    {
        StringBuilder sb = new StringBuilder("[");

        for (Iterator<Object> it = Arrays.asList(args).iterator(); it.hasNext(); ) {
            try {
                sb.append(String.valueOf(it.next()));
            }
            catch (Exception e) {
                sb.append("<").append(e.getClass().getSimpleName()).append(">");
            }

            if (it.hasNext()) {
                sb.append(",");
            }
        }

        return sb.append("]").toString();
    }
}
