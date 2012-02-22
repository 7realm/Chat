package my.chat.model.user;

import java.util.Date;

import my.chat.model.commons.ChatEntity;

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