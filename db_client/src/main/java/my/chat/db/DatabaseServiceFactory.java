/**
 * 
 */
package my.chat.db;

import my.chat.commons.ServiceFactory;
import my.chat.db.memory.MemoryDatabaseServiceFactory;
import my.chat.exceptions.ChatException;

/**
 * Builds database service.
 * <p>
 * <b>Thread safe:</b> No.
 * 
 * @author 7realm
 */
public abstract class DatabaseServiceFactory extends ServiceFactory<DatabaseService> {

    public static DatabaseService getInstance() throws ChatException {
        ServiceFactory<DatabaseService> factory = new MemoryDatabaseServiceFactory();

        return factory.buildService();
    }

}
