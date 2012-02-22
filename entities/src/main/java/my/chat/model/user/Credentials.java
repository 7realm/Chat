package my.chat.model.user;

import javax.persistence.Embeddable;

import my.chat.model.commons.ChatEntity;
import my.chat.model.commons.UpdateChatException;

/**
 * User credentials that will store login information in database.
 * <p>
 * <b>Thread safe:</b> No.
 * 
 * @author 7realm
 */
@Embeddable
public class Credentials extends ChatEntity {
    private static final long serialVersionUID = 8700004254749555640L;

    /** The login username. */
    private String username;

    /** The user password. */
    private String password;

    /**
     * @param username
     * @param password
     */
    public Credentials(String username, String password) {
        super();
        this.username = username;
        this.password = password;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public void update(ChatEntity newEntity) throws UpdateChatException {
        super.update(newEntity);

        Credentials newCredentials = (Credentials) newEntity;

        setUsername(newCredentials.getUsername());
        setPassword(newCredentials.getPassword());
    }
}
