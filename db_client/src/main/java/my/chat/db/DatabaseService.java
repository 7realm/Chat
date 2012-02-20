package my.chat.db;

import my.chat.model.user.User;

/**
 * This is database service contract, that will define database service.
 * <p>
 * <b>Thread safe:</b> No.
 * 
 * @author 7realm
 */
public interface DatabaseService {

    /**
     * Get user by ID from database.
     * 
     * @param id
     * @return
     */
    User getUser(long id);

    /**
     * Check user credentials.
     * 
     * @param username
     * @param password
     * @return
     * @throws PersistanceChatException
     */
    User login(String username, String password) throws PersistanceChatException;

    /**
     * Create user in database.
     * 
     * @param username
     * @param password
     * @return
     * @throws PersistanceChatException
     */
    User createUser(String username, String password) throws PersistanceChatException;

    /**
     * Update user in database.
     * 
     * @param user
     * @throws PersistanceChatException
     */
    void updateUser(User user) throws PersistanceChatException;
}
