package my.chat.commons;

import java.util.Arrays;

import my.chat.exceptions.ConfigurationChatException;

/**
 * Helper class for argument checking.
 * 
 * @author taras.kovalchuk
 */
public final class ArgumentHelper {

    private ArgumentHelper() {
        // empty
    }

    public static void checkNotNull(String name, Object value) {
        if (value == null) {
            throw new IllegalArgumentException("Argument '" + name + "' should not be null.");
        }
    }

    public static void checkString(String name, String value) {
        checkNotNull(name, value);
        if (value.trim().length() == 0) {
            throw new IllegalArgumentException("Argument '" + name + "' should not be empty string.");
        }
    }

    public static void checkPositive(String name, int value) {
        if (value <= 0) {
            throw new IllegalArgumentException("Argument '" + name + "' should not be zero or negative.");
        }
    }

    public static void checkState(Enum<?> value, Enum<?>... expectedValues) {
        // if value is present in expected values then exit method
        for (Enum<?> expectedValue : expectedValues) {
            if (expectedValue == value) {
                return;
            }
        }

        // if not found
        throw new IllegalStateException("Instance is in state '" + value + "', but one of " + Arrays.toString(expectedValues)
            + " expected.");
    }

    public static void checkState(String fieldName, Object value) {
        if (value == null) {
            throw new IllegalStateException("Feild '" + fieldName + "', is not set.");
        }
    }

    public static void checkInit(String fieldName, Object fieldValue) {
        if (fieldValue == null) {
            throw new ConfigurationChatException("Field '%1' is not set.", fieldName);
        }
    }
}
