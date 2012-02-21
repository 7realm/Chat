/**
 * 
 */
package my.chat.db.file;

import static my.chat.commons.ArgumentHelper.checkInit;
import my.chat.db.DatabaseService;
import my.chat.db.DatabaseServiceFactory;
import my.chat.exceptions.ChatException;

/**
 * 
 * <p>
 * <b>Thread safe:</b> No.
 *
 * @author 7realm
 */
public class FileDatabaseServiceFactory extends DatabaseServiceFactory {
    private String databaseFileName = "./database";
    
    public FileDatabaseServiceFactory() {
        // empty
    }

    public String getDatabaseFileName() {
        return databaseFileName;
    }
    
    public void setDatabaseFileName(String databaseFileName) {
        this.databaseFileName = databaseFileName;
    }
    
    protected void init() {
        checkInit("databaseFileName", databaseFileName);
    }

    @Override
    public DatabaseService buildService() throws ChatException {
        return new FileDatabaseService(databaseFileName);
    }

}
