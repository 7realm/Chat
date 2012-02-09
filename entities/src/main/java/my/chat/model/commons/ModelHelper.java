/**
 * 
 */
package my.chat.model.commons;

import java.util.Collection;

/**
 * Helper for Model classes.
 * <p>
 * <b>Thread safe:</b> Yes, this is static class.
 * 
 * @author 7realm
 */
public final class ModelHelper {

    public static void memberExist(ChatEntity entity, String memberName, Object value) {
        if (value == null) {
            throw new InvalidMemeberException("Entity '" + entity + "' has missing member '" + memberName + "'.");
        }
    }
    
    public static void memberAtLeastOne(ChatEntity entity, String memberName, Collection<?> value) {
        if (value.size() == 0) {
            throw new InvalidMemeberException("Entity '" + entity + "' has empty member '" + memberName + "'.");
        }
    }
}
