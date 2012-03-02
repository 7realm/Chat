package my.chat.model.user;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import my.chat.model.ChatEntity;
import my.chat.model.ChatIdEntity;
import my.chat.model.UpdateChatException;

/**
 * Chat user entity.
 * <p>
 * <b>Thread safe:</b> No.
 * 
 * @author 7realm
 */
@NamedQueries({
    @NamedQuery(name = "loginUser", query = "SELECT u FROM User u WHERE u.credentials.username = :username AND u.credentials.password = :password"),
    @NamedQuery(name = "countUsersByName", query = "SELECT COUNT(u) FROM User u WHERE u.nickname = :username") })
@Entity
@Table(name = "user")
public class User extends ChatIdEntity {
    private static final long serialVersionUID = -6808087150087966591L;

    /** The user nick name. */
    private String nickname;

    /** The credentials. */
    @Embedded
    private Credentials credentials;

    /** The list of user's contacts. */
    @Transient
    private List<Contact> contacts = new ArrayList<Contact>();

    /** List of user's statuses. */
    @Transient
    private List<Status> statuses = new ArrayList<Status>();

    public User() {
        // empty
    }

    public User(String username, String password) {
        nickname = username;
        credentials = new Credentials(username, password);
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public List<Status> getStatuses() {
        return statuses;
    }

    @Override
    public void update(ChatEntity newEntity) throws UpdateChatException {
        super.update(newEntity);

        User newUser = (User) newEntity;

        setNickname(newUser.getNickname());
        setCredentials(newUser.getCredentials());

        contacts = newUser.getContacts();
        statuses = newUser.getStatuses();
    }

    @Override
    public User createTransferObject() throws UpdateChatException {
        User result = (User) super.createTransferObject();

        // remove contacts, because they can cause infinitive loop
        result.getContacts().clear();

        return result;
    }
}