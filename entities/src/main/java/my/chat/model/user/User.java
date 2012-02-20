package my.chat.model.user;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import my.chat.model.commons.ChatEntity;
import my.chat.parser.FieldDataIgnore;
import my.chat.parser.ObjectData;

/**
 * 
 * <p>
 * <b>Thread safe:</b> No.
 * 
 * @author 7realm
 */
@NamedQueries({
    @NamedQuery(name = "loginUser", query = "SELECT u FROM User u WHERE u.username = :username AND u.password = :password"),
    @NamedQuery(name = "countUsersByName", query = "SELECT COUNT(u) FROM User u WHERE u.username = :username") })
@Entity
@Table(name = "user")
@ObjectData
public class User implements ChatEntity {
    private static final long serialVersionUID = -6808087150087966591L;

    /** The user ID. */
    @Id
    private long userId;

    /** The user nick name. */
    private String nickname;

    /** The credentials. */
    @Embedded
    @FieldDataIgnore
    private UserCredentials userCredentials;

    /** The list of user's contacts. */
    @Transient
    private List<UserContact> contacts = new ArrayList<UserContact>();

    /** List of user's statuses. */
    @Transient
    private List<Status> statuses = new ArrayList<Status>();

    public User() {
        // empty
    }

    public User(String username, String password) {
        nickname = username;
        userCredentials = new UserCredentials(username, password);
    }

    public long getUserId() {
        return userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public UserCredentials getUserCredentials() {
        return userCredentials;
    }

    public void setUserCredentials(UserCredentials userCredentials) {
        this.userCredentials = userCredentials;
    }

    public List<UserContact> getContacts() {
        return contacts;
    }

    public List<Status> getStatuses() {
        return statuses;
    }

    /**
     * User status, can only exist for User.
     * <p>
     * <b>Thread safe:</b> No.
     * 
     * @author 7realm
     */
    public class Status implements ChatEntity {
        private static final long serialVersionUID = 1L;

        /** Status content. */
        private String content;

        /** Status create date. */
        private Date createDate;

        /**
         * @param content
         * @param createDate
         */
        public Status(String content, Date createDate) {
            this.content = content;
            this.createDate = createDate;
        }

        /**
         * @return the content
         */
        public String getContent() {
            return content;
        }

        /**
         * @return the createDate
         */
        public Date getCreateDate() {
            return createDate;
        }

    }
}