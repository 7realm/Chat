package my.chat.logging;

import my.chat.commons.FilePaths;
import my.chat.commons.Helper;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class Log {
    private static final Logger LOGGER;
    
    static {
        PropertyConfigurator.configure(FilePaths.LOG_PROPERTIES);
        
        LOGGER = Logger.getLogger("chat");
    }

    public static void info(Object callerClass, String message, Object... params) {
        LOGGER.info(makeMessage(callerClass, message, params));
    }

    public static <T extends Throwable> void info(Object callerClass, T throwable, String message, Object... params) {
        LOGGER.info(makeMessage(callerClass, message, params), throwable);
    }

    public static void debug(Object callerClass, String message, Object... params) {
        LOGGER.debug(makeMessage(callerClass, message, params));
    }

    public static <T extends Throwable> T debug(Object callerClass, T throwable, String message, Object... params) {
        LOGGER.debug(makeMessage(callerClass, message, params), throwable);
        return throwable;
    }

    public static void warn(Object callerClass, String message, Object... params) {
        LOGGER.warn(makeMessage(callerClass, message, params));
    }

    public static <T extends Throwable> T warn(Object callerClass, T throwable, String message, Object... params) {
        LOGGER.warn(makeMessage(callerClass, message, params), throwable);
        return throwable;
    }

    public static void error(Object callerClass, String message, Object... params) {
        LOGGER.error(makeMessage(callerClass, message, params));
    }

    public static <T extends Throwable> T error(Object callerClass, T throwable, String message, Object... params) {
        LOGGER.error(makeMessage(callerClass, message, params), throwable);
        return throwable;
    }

    public static <T extends Throwable> T error(Object callerClass, T throwable) {
        LOGGER.error(throwable.getMessage(), throwable);
        return throwable;
    }

    private static String makeMessage(Object callerClass, String message, Object... params) {
        message = Helper.makeMessage(message, params);
        return (callerClass == null ? "" : callerClass.getClass().getName() + ": ") + message;
    }
}
