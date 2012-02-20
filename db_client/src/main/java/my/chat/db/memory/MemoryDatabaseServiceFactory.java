/**
 * 
 */
package my.chat.db.memory;

import my.chat.db.DatabaseService;
import my.chat.db.DatabaseServiceFactory;
import my.chat.exceptions.ChatException;

/**
 * 
 * TODO: close file.
 * <p>
 * <b>Thread safe:</b> No.
 * 
 * @author 7realm
 */
public class MemoryDatabaseServiceFactory extends DatabaseServiceFactory {
    /**
     * 
     * @return
     * @throws ChatException
     */
    @Override
    public DatabaseService buildService() throws ChatException {
        return new MemoryDatabaseService();
    }

}
