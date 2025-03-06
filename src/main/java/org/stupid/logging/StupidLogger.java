/*
 * MIT License
 *
 * Copyright (c) 2025 Tanmay Majumdar
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package org.stupid.logging;


import java.nio.charset.Charset;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StupidLogger {

    private final Logger log;

    private StupidLogger(Logger log) {
        this.log = log;
    }

    public static StupidLogger getLogger(final String myClass) {
        return new StupidLogger(Logger.getLogger(myClass));
    }

    public void info(final String msg, final Object... obj) {
        log.info(String.format(Locale.ENGLISH, msg, obj));
    }

    public void fine(final String msg, final Object... obj) {
        log.fine(String.format(msg, obj));
    }

    public void finest(final String msg, final Object... obj) {
        log.finest(String.format(msg, obj));
    }

    public void warn(final String msg, final Object... obj) {
        log.warning(String.format(msg, obj));
    }
    public void error(final String msg, final Object... obj) {
        log.severe(String.format(msg, obj));
    }
}
