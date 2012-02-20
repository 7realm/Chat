package my.chat.model.user;

import javax.persistence.Embeddable;

/**
 * User credentials that will store login information in database.
 * <p>
 * <b>Thread safe:</b> No.
 *
 * @author 7realm
 */
@Embeddable
public class UserCredentials {
    
    /** The login username. */
    private String username;
    
    /** The user password. */
    private String password;

    /**
     * @param username
     * @param password
     */
    public UserCredentials(String username, String password) {
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
}
