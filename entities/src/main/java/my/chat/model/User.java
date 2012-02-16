package my.chat.model;

import static my.chat.commons.ArgumentHelper.checkString;
import static my.chat.model.commons.ModelHelper.memberAtLeastOne;
import static my.chat.model.commons.ModelHelper.memberExist;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import my.chat.model.commons.ChatEntity;
import my.chat.parser.FieldDataIgnore;
import my.chat.parser.ObjectData;

/**
 * Data container for chat user.
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
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true, nullable = false, name = "iduser")
    private long userId;

    /** The user name. */
    @Column(nullable = false, length = 50)
    private String username;

    /** The user password. */
    @Column(nullable = false, length = 50)
    @FieldDataIgnore
    private String password;

    /** The list of user's contacts. */
    @Transient
    private List<User> contacts = new ArrayList<User>();

    /** List of user's statuses. */
    @Transient
    private List<Status> statuses = new ArrayList<Status>();

    public User() {
        // empty
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public long getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public List<User> getContacts() {
        memberExist(this, "contacts", contacts);

        return contacts;
    }

    public Status getLastStatus() {
        memberExist(this, "statuses", statuses);
        memberAtLeastOne(this, "statuses", statuses);

        return statuses.get(statuses.size() - 1);
    }

    public void addStatus(String content) {
        checkString("content", content);
        memberExist(this, "statuses", statuses);

        // add status with current date
        statuses.add(new Status(content, new Date()));

        // TODO notify
    }

    public void addContact(User user) {

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