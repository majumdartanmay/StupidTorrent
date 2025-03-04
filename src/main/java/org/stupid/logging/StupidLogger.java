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
