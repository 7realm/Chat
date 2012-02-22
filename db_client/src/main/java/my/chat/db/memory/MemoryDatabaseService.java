package my.chat.db.memory;

import static my.chat.commons.ArgumentHelper.checkNotNull;
import static my.chat.commons.ArgumentHelper.checkString;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import my.chat.db.DatabaseService;
import my.chat.db.PersistanceChatException;
import my.chat.db.SecurityChatException;
import my.chat.model.user.User;

public class MemoryDatabaseService implements DatabaseService {

    private final Map<Long, User> database = Collections.synchronizedMap(new HashMap<Long, User>());
    private AtomicLong lastUserId = new AtomicLong(0);

    public MemoryDatabaseService() {
        // add sample data
        doCreateUser("ivern", "secret");
        doCreateUser("peter", "valid");
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
        if (user.getCredentials().getPassword().equals(password)) {
            return user;
        }
        throw new SecurityChatException("Incorrect password for user %1.", username);
    }

    @Override
    public User createUser(String username, String password) throws PersistanceChatException {
        checkString("username", username);
        checkString("password", password);

        if (getUser(username) != null) {
            throw new PersistanceChatException("User %1 is already present in database.", username);
        }

        User user = doCreateUser(username, password);

        return user;
    }

    @Override
    public void updateUser(User user) throws PersistanceChatException {
        checkNotNull("user", user);

        if (getUser(user.getId()) == null) {
            throw new PersistanceChatException("User %1 is missinf in database.", user.getNickname());
        }

        database.put(user.getId(), user);
    }

    private User doCreateUser(String username, String password) {
        User user = new User(username, password);
        user.setId(lastUserId.incrementAndGet());
        database.put(user.getId(), user);
        return user;
    }

    private User getUser(String username) {
        for (User user : database.values()) {
            if (user.getCredentials().getUsername().equals(username)) {
                return user;
            }
        }

        return null;
    }
}
