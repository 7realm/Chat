package my.chat.model.user;

import my.chat.model.commons.ChatEntity;
import my.chat.parser.ObjectData;

/**
 * Defines user's chat contact.
 * <p>
 * <b>Thread safe:</b> No.
 * 
 * @author 7realm
 */
@ObjectData
public class UserContact implements ChatEntity {
    private static final long serialVersionUID = -5759858935482127751L;

    /** The custom name of contact, only visible to contacts owner. */
    private String givenName;

    /** The actual user, that represents contact. */
    private User user;

    /**
     * Empty default constructor.
     */
    public UserContact() {
        // empty default constructor
    }

    /**
     * @param givenName
     * @param user
     */
    public UserContact(String givenName, User user) {
        this.givenName = givenName;
        this.user = user;
    }

    /**
     * @return the givenName
     */
    public String getGivenName() {
        return givenName;
    }

    /**
     * @param givenName the givenName to set
     */
    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    /**
     * @return the user
     */
    public User getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
    }

}
