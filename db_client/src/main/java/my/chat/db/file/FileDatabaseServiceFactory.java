/**
 * 
 */
package my.chat.db.file;

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
public class FileDatabaseServiceFactory extends DatabaseServiceFactory {

    private static final String DATABASE_FILE_NAME = "./database";

    /**
     * 
     * @return
     * @throws ChatException
     */
    @Override
    public DatabaseService buildService() throws ChatException {
        return new FileDatabaseService(DATABASE_FILE_NAME);
    }

}
