/**
 * 
 */
package my.chat.commons;

import my.chat.exceptions.ChatException;


/**
 * 
 * <p>
 * <b>Thread safe:</b> No.
 * 
 * @author 7realm
 */
public abstract class ServiceFactory<T> {

    public abstract T buildService() throws ChatException;
}
