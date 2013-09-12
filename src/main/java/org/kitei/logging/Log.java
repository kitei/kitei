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

import org.kitei.internal.FormattingUtils;
import org.kitei.system.SystemPropertyKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;

import java.util.IllegalFormatException;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

/**
 * Wraps a slf4j logger into a number of convenience methods such as varargs.
 */
public final class Log
{
    public enum LogLevel
    {
        TRACE,
        DEBUG,
        INFO,
        WARN,
        ERROR
    };

    private static final String LOG_NAME = Log.class.getName();

    private final Logger wrappedLogger;
    private final boolean trimExceptions;

    /**
     * Finds the logger for the current class by using the call stack.
     */
    public static Log findLog()
    {
        return findLog(0);
    }

    /**
     * Finds the logger for the caller by using the call stack.
     */
    public static Log findCallerLog()
    {
        return findLog(1);
    }

    /**
     * Returns a Logger for a given class.
     */
    public static Log forClass(final Class<?> clazz)
    {
        Preconditions.checkNotNull(clazz, "clazz");
        return new Log(LoggerFactory.getLogger(clazz));
    }

    /**
     * Returns a Logger for a given category name.
     */
    public static Log forCategory(final String category)
    {
        Preconditions.checkNotNull(category, "category");
        return new Log(LoggerFactory.getLogger(category));
    }

    /**
     * Returns a Logger for a given log4j logger.
     */
    public static Log forLogger(final Logger wrappedLogger)
    {
        return new Log(wrappedLogger);
    }

    private static Log findLog(final int depth)
    {
        final StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
        int i = 1;
        for (; i < stacktrace.length && stacktrace[i].getClassName().equals(LOG_NAME); i++) {
            // empty.
        }
        if (i + depth < stacktrace.length) {
            return forCategory(stacktrace[i + depth].getClassName());
        }
        throw new IllegalStateException(format("Attempt to generate a logger for an invalid depth (%d vs. %d).", depth, stacktrace.length - i));
    }

    private Log(final Logger wrappedLogger)
    {
        this.wrappedLogger = checkNotNull(wrappedLogger, "wrappedLogger");
        this.trimExceptions = Boolean.getBoolean(SystemPropertyKeys.LOGGING_TRIM_EXCEPTIONS);
    }

    @VisibleForTesting
    Logger getWrappedLogger()
    {
        return wrappedLogger;
    }

    // ========================================================================
    //
    // Level mgt.
    //
    // ========================================================================

    public boolean isTraceEnabled()
    {
        return wrappedLogger.isTraceEnabled();
    }

    public boolean isDebugEnabled()
    {
        return wrappedLogger.isDebugEnabled();
    }

    public boolean isInfoEnabled()
    {
        return wrappedLogger.isInfoEnabled();
    }

    public boolean isWarnEnabled()
    {
        return wrappedLogger.isWarnEnabled();
    }

    public boolean isErrorEnabled()
    {
        return wrappedLogger.isErrorEnabled();
    }

    // ========================================================================
    //
    // Trace level methods
    //
    // ========================================================================

    public void trace(final String message)
    {
        wrappedLogger.trace(message);
    }

    public void trace(final Throwable t)
    {
        wrappedLogger.trace("", t);
    }

    public void trace(final Throwable t, final String message)
    {
        wrappedLogger.trace(message, t);
    }

    public void trace(final String message, final Object... args)
    {
        if (wrappedLogger.isTraceEnabled()) {
            wrappedLogger.trace(safeFormat(LogLevel.TRACE, message, args));
        }
    }

    public void trace(final Throwable t, final String message, final Object... args)
    {
        if (wrappedLogger.isTraceEnabled()) {
            wrappedLogger.trace(safeFormat(LogLevel.TRACE, message, args), t);
        }
    }

    // ========================================================================
    //
    // Debug level methods
    //
    // ========================================================================

    public void debug(final String message)
    {
        wrappedLogger.debug(message);
    }

    public void debug(final Throwable t)
    {
        wrappedLogger.debug("", t);
    }

    public void debug(final Throwable t, final String message)
    {
        wrappedLogger.debug(message, t);
    }

    public void debug(final String message, final Object... args)
    {
        if (wrappedLogger.isDebugEnabled()) {
            wrappedLogger.debug(safeFormat(LogLevel.DEBUG, message, args));
        }
    }

    public void debug(final Throwable t, final String message, final Object... args)
    {
        if (wrappedLogger.isDebugEnabled()) {
            wrappedLogger.debug(safeFormat(LogLevel.DEBUG, message, args), t);
        }
    }

    // ========================================================================
    //
    // Info level methods
    //
    // ========================================================================

    public void info(final String message)
    {
        wrappedLogger.info(message);
    }

    public void info(final Throwable t)
    {
        if (!trimExceptions) {
            wrappedLogger.info("", t);
        }
        else {
            if (wrappedLogger.isDebugEnabled()) {
                wrappedLogger.info("", t);
            }
            else {
                wrappedLogger.info(summarize(LogLevel.INFO, t, ""));
            }
        }
    }

    public void info(final Throwable t, final String message)
    {
        if (!trimExceptions) {
            wrappedLogger.info(message, t);
        }
        else {
            if (wrappedLogger.isDebugEnabled()) {
                wrappedLogger.info(message, t);
            }
            else {
                wrappedLogger.info(summarize(LogLevel.INFO, t, message));
            }
        }
    }

    public void info(final String message, final Object... args)
    {
        if (wrappedLogger.isInfoEnabled()) {
            wrappedLogger.info(safeFormat(LogLevel.INFO, message, args));
        }
    }

    public void info(final Throwable t, final String message, final Object... args)
    {
        if (!trimExceptions) {
            if (wrappedLogger.isInfoEnabled()) {
                wrappedLogger.info(safeFormat(LogLevel.INFO, message, args), t);
            }
        }
        else {
            if (wrappedLogger.isDebugEnabled()) {
                wrappedLogger.info(safeFormat(LogLevel.INFO, message, args), t);
            }
            else if (wrappedLogger.isInfoEnabled()) {
                wrappedLogger.info(summarize(LogLevel.INFO, t, message, args));
            }
        }
    }

    // ========================================================================
    //
    // Warn level methods
    //
    // ========================================================================

    public void warn(final String message)
    {
        wrappedLogger.warn(message);
    }

    public void warn(final Throwable t)
    {
        if (!trimExceptions) {
            wrappedLogger.warn("", t);
        }
        else {
            if (wrappedLogger.isDebugEnabled()) {
                wrappedLogger.warn("", t);
            }
            else {
                wrappedLogger.warn(summarize(LogLevel.WARN, t, ""));
            }
        }
    }

    public void warn(final Throwable t, final String message)
    {
        if (!trimExceptions) {
            wrappedLogger.warn(message, t);
        }
        else {
            if (wrappedLogger.isDebugEnabled()) {
                wrappedLogger.warn(message, t);
            }
            else {
                wrappedLogger.warn(summarize(LogLevel.WARN, t, message));
            }
        }
    }

    public void warn(final String message, final Object... args)
    {
        if (isWarnEnabled()) {
            wrappedLogger.warn(safeFormat(LogLevel.WARN, message, args));
        }
    }

    public void warn(final Throwable t, final String message, final Object... args)
    {
        if (!trimExceptions) {
            if (isWarnEnabled()) {
                wrappedLogger.warn(safeFormat(LogLevel.WARN, message, args), t);
            }
        }
        else {
            if (wrappedLogger.isDebugEnabled()) {
                wrappedLogger.warn(safeFormat(LogLevel.WARN, message, args), t);
            }
            else if (isWarnEnabled()) {
                wrappedLogger.warn(summarize(LogLevel.WARN, t, message, args));
            }
        }
    }

    // ========================================================================
    //
    // Error level methods
    //
    // ========================================================================

    public void error(final String message)
    {
        wrappedLogger.error(message);
    }

    public void error(final Throwable t)
    {
        if (!trimExceptions) {
            wrappedLogger.error("", t);
        }
        else {
            if (wrappedLogger.isDebugEnabled()) {
                wrappedLogger.error("", t);
            }
            else {
                wrappedLogger.error(summarize(LogLevel.ERROR, t, ""));
            }
        }
    }

    public void error(final Throwable t, final String message)
    {
        if (!trimExceptions) {
            wrappedLogger.error(message, t);
        }
        else {
            if (wrappedLogger.isDebugEnabled()) {
                wrappedLogger.error(message, t);
            }
            else {
                wrappedLogger.error(summarize(LogLevel.ERROR, t, message));
            }
        }
    }

    public void error(final String message, final Object... args)
    {
        if (isErrorEnabled()) {
            wrappedLogger.error(safeFormat(LogLevel.ERROR, message, args));
        }
    }

    public void error(final Throwable t, final String message, final Object... args)
    {
        if (!trimExceptions) {
            if (isErrorEnabled()) {
                wrappedLogger.error(safeFormat(LogLevel.ERROR, message, args), t);
            }
        }
        else {
            if (wrappedLogger.isDebugEnabled()) {
                wrappedLogger.error(safeFormat(LogLevel.ERROR, message, args), t);
            }
            else if (isErrorEnabled()) {
                wrappedLogger.error(summarize(LogLevel.ERROR, t, message, args));
            }
        }
    }

    // ========================================================================
    //
    // log methods
    //
    // ========================================================================

    public void log(final LogLevel level, final String message)
    {
        switch (level) {
            case TRACE:
                trace(message);
                break;

            case DEBUG:
                debug(message);
                break;

            case INFO:
                info(message);
                break;

            case WARN:
                warn(message);
                break;

            case ERROR:
                error(message);
                break;
        }
    }

    public void log(final LogLevel level, final Throwable t)
    {
        switch (level) {
            case TRACE:
                trace(t);
                break;

            case DEBUG:
                debug(t);
                break;

            case INFO:
                info(t);
                break;

            case WARN:
                warn(t);
                break;

            case ERROR:
                error(t);
                break;
        }
    }

    public void log(final LogLevel level, final Throwable t, final String message)
    {
        switch (level) {
            case TRACE:
                trace(t, message);
                break;

            case DEBUG:
                debug(t, message);
                break;

            case INFO:
                info(t, message);
                break;

            case WARN:
                warn(t, message);
                break;

            case ERROR:
                error(t, message);
                break;
        }
    }

    public void log(final LogLevel level, final String message, final Object... args)
    {
        switch (level) {
            case TRACE:
                trace(message, args);
                break;

            case DEBUG:
                debug(message, args);
                break;

            case INFO:
                info(message, args);
                break;

            case WARN:
                warn(message, args);
                break;

            case ERROR:
                error(message, args);
                break;
        }
    }

    public void log(final LogLevel level, final Throwable t, final String message, final Object... args)
    {
        switch (level) {
            case TRACE:
                trace(t, message, args);
                break;

            case DEBUG:
                debug(t, message, args);
                break;

            case INFO:
                info(t, message, args);
                break;

            case WARN:
                warn(t, message, args);
                break;

            case ERROR:
                error(t, message, args);
                break;
        }
    }

    // ========================================================================

    private String summarize(final LogLevel level, final Throwable t, final String msg, final Object... args)
    {
        final String message = (t == null) ? null : t.getMessage();

        if (message == null) {
            return safeFormat(level, msg, args);
        }

        final int index = message.indexOf('\n');

        if (index == -1) {
            return safeFormat(level, msg, args) + ": " + message;
        }

        final String shortMsg = message.substring(0, index);
        return safeFormat(level, msg, args) + " (Switch to DEBUG for full stack trace): " + shortMsg;
    }

    private String safeFormat(final LogLevel level, final String msg, final Object... args)
    {
        if (msg == null) {
            return args.length == 0 ? "" : FormattingUtils.safeArgs(args);
        }
        else {
            try {
                return format(msg, args);
            }
            catch (IllegalFormatException ife) {
                wrappedLogger.error(format("Invalid format string while logging: %s '%s' %s", level, msg, FormattingUtils.safeArgs(args)));
            }
            return format("'%s' %s", msg, FormattingUtils.safeArgs(args));
        }
    }
}
