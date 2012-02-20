/**
 * 
 */
package my.chat.db.file;

import static my.chat.commons.ArgumentHelper.checkNotNull;
import static my.chat.commons.ArgumentHelper.checkString;
import static my.chat.commons.Helper.close;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import my.chat.db.DatabaseService;
import my.chat.db.PersistanceChatException;
import my.chat.db.SecurityChatException;
import my.chat.logging.Log;
import my.chat.model.user.User;
import my.chat.model.user.UserCredentials;

/**
 * 
 * <p>
 * <b>Thread safe:</b> No.
 * 
 * @author 7realm
 */
public class FileDatabaseService implements DatabaseService {
    private static final String DATA_SPLITTER = "|";
    private static final String EOL = System.getProperty("line.separator");
    private final Map<Long, User> database = Collections.synchronizedMap(new HashMap<Long, User>());
    private AtomicLong lastUserId = new AtomicLong(0);
    private File databaseFile;

    public FileDatabaseService(String databaseFilename) throws PersistanceChatException {
        checkString("databaseFile", databaseFilename);

        databaseFile = new File(databaseFilename);
        if (!databaseFile.exists()) {
            Log.warn(this, "Database is empty, recreating.");

            // add sample data
            createUser("ivern", "secret");
            createUser("peter", "valid");

            save();
        } else {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(databaseFilename));
                String line = reader.readLine();
                lastUserId.set(Long.parseLong(line));

                while (true) {
                    line = reader.readLine();
                    if (line == null) {
                        break;
                    }

                    String[] parts = line.split(DATA_SPLITTER);
                    User user = new User();
                    user.setUserId(Long.parseLong(parts[0]));
                    user.setNickname(parts[1]);
                    user.setUserCredentials(new UserCredentials(parts[2], parts[3]));

                    database.put(user.getUserId(), user);
                }
            } catch (IOException e) {
                throw new PersistanceChatException("Failed to read data from file %1.", databaseFilename);
            } catch (NumberFormatException e) {
                throw new PersistanceChatException("Failed to parse user id at file %1.", databaseFilename);
            } finally {
                close(reader);
            }
        }
    }

    @Override
    public User getUser(long id) {
        return database.get(id);
    }

    @Override
    public User login(String username, String password) throws PersistanceChatException {
        checkString("username", username);
        checkString("password", password);

        User user = getUser(username);
        if (user == null) {
            throw new SecurityChatException("User %1 was not found.", username);
        }
        if (user.getUserCredentials().getPassword().equals(password)) {
            return user;
        }
        throw new SecurityChatException("Incorrect password for user %1.", username);
    }

    @Override
    public User createUser(String username, String password) throws PersistanceChatException {
        checkString("username", username);
        checkString("password", password);

        User user = new User(username, password);
        user.setUserId(lastUserId.incrementAndGet());
        database.put(user.getUserId(), user);

        save();

        return user;
    }

    @Override
    public void updateUser(User user) throws PersistanceChatException {
        checkNotNull("user", user);

        if (getUser(user.getUserId()) == null) {
            throw new PersistanceChatException("User %1 is missinf in database.", user.getNickname());
        }

        database.put(user.getUserId(), user);

        save();
    }

    private void save() throws PersistanceChatException {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(databaseFile));
            writer.write(lastUserId.get() + EOL);
            for (User user : database.values()) {
                writer.write(user.getUserId() + DATA_SPLITTER + user.getNickname() + DATA_SPLITTER
                    + user.getUserCredentials().getUsername() + DATA_SPLITTER
                    + user.getUserCredentials().getPassword() + EOL);
            }
        } catch (IOException e) {
            throw new PersistanceChatException("Failed to write data to file %1.", databaseFile.getAbsolutePath());
        } finally {
            close(writer);
        }
    }

    private User getUser(String username) {
        for (User user : database.values()) {
            if (user.getUserCredentials().getUsername().equals(username)) {
                return user;
            }
        }

        return null;
    }

}
