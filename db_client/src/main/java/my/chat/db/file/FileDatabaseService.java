/**
 * 
 */
package my.chat.db.file;

import static my.chat.commons.ArgumentHelper.checkString;
import static my.chat.commons.Helper.close;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import my.chat.db.DatabaseService;
import my.chat.db.PersistanceChatException;
import my.chat.logging.Log;
import my.chat.model.user.User;

/**
 * 
 * <p>
 * <b>Thread safe:</b> No.
 * 
 * @author 7realm
 */
public class FileDatabaseService implements DatabaseService {

    public FileDatabaseService(String databaseFile) throws PersistanceChatException {
        checkString("databaseFile", databaseFile);
        
        File file = new File(databaseFile);
        if (!file.exists()) {
            Log.warn(this, "Database is empty, recreating.");
        }
        
        
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(databaseFile));
        } catch (IOException e) {
            throw new PersistanceChatException("Failed to read data from file %1.", databaseFile);
        } finally {
            close(reader);
        }
    }

    @Override
    public User getUser(long id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public User login(String username, String password) throws PersistanceChatException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public User createUser(String username, String password) throws PersistanceChatException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void updateUser(User user) throws PersistanceChatException {
        // TODO Auto-generated method stub

    }

}
