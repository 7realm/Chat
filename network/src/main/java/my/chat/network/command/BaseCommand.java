package my.chat.network.command;

import java.util.Collection;

import my.chat.commands.CommandType;

/**
 * Base type for command transport object.
 * <p>
 * <b>Thread safe:</b> No.
 * 
 * @author 7realm
 */
public abstract class BaseCommand {
    public abstract CommandType getType();

    protected static void checkIsSet(String itemName, Object value) throws CommandContentException {
        if (value == null) {
            throw new CommandContentException("Object %1 is not set.", itemName);
        }
    }

    protected static void checkIsEmpty(String itemName, Object value) throws CommandContentException {
        checkIsSet(itemName, value);

        // check empty string
        if (value instanceof String && ((String) value).trim().length() == 0) {
            throw new CommandContentException("String %1 is trimmed empty,", itemName);
        }

        // check empty collection
        if (value instanceof Collection<?> && ((Collection<?>) value).size() == 0) {
            throw new CommandContentException("Collection %1 is empty.", itemName);
        }
    }
}
