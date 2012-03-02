/**
 * 
 */
package my.chat.network.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that will show what fields must be ignored.
 * <p>
 * It is used for fields of {@link BaseCommand} ancestors.
 * <p>
 * <b>Thread safe:</b> No.
 *
 * @author 7realm
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.FIELD })
public @interface Ignore {

}
