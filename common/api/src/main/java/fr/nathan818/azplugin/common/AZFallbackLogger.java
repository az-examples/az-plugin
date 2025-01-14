package fr.nathan818.azplugin.common;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import lombok.Getter;

final class AZFallbackLogger extends Logger {

    @Getter
    private static final AZFallbackLogger logger = new AZFallbackLogger();

    private AZFallbackLogger() {
        super(AZFallbackLogger.class.getCanonicalName(), null);
        addHandler(new FallbackHandler());
        setLevel(Level.ALL);
    }

    private class FallbackHandler extends Handler {

        public FallbackHandler() {
            setFormatter(new FallbackFormatter());
        }

        @Override
        public void publish(LogRecord lr) {
            if (!isLoggable(lr)) {
                return;
            }
            PrintStream stream;
            if (lr.getLevel().intValue() >= Level.WARNING.intValue()) {
                stream = System.err;
            } else {
                stream = System.out;
            }
            // Use println() instead of print() to fix some logging issues with Bukkit/Spigot
            stream.println(removeTrailingNewline(getFormatter().format(lr)));
            stream.flush();
        }

        @Override
        public void flush() {}

        @Override
        public void close() {}
    }

    private static final class FallbackFormatter extends SimpleFormatter {

        @Override
        public String format(LogRecord lr) {
            StringBuilder sb = new StringBuilder().append("[AZPlugin] ").append(formatMessage(lr)).append('\n');
            Throwable ex = lr.getThrown();
            if (ex != null) {
                StringWriter sw = new StringWriter();
                ex.printStackTrace(new PrintWriter(sw));
                sb.append(sw);
            }
            return sb.toString();
        }
    }

    private static String removeTrailingNewline(String str) {
        if (!str.isEmpty() && str.charAt(str.length() - 1) == '\n') {
            return str.substring(0, str.length() - 1);
        }
        return str;
    }
}
