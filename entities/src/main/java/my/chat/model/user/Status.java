package my.chat.model.user;

import java.util.Date;

import my.chat.model.commons.ChatEntity;
import my.chat.model.commons.ChatIdEntity;
import my.chat.model.commons.UpdateChatException;

/**
 * User status, can only exist for User.
 * <p>
 * <b>Thread safe:</b> No.
 * 
 * @author 7realm
 */
public class Status extends ChatIdEntity {
    private static final long serialVersionUID = -3062473954080402482L;

    /** Status content. */
    private String content;

    /** Status create date. */
    private Date createDate;

    public Status() {

    }

    /**
     * @param content
     * @param createDate
     */
    public Status(String content, Date createDate) {
        this.content = content;
        this.createDate = createDate;
    }

    public String getContent() {
        return content;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Override
    public void update(ChatEntity newEntity) throws UpdateChatException {
        super.update(newEntity);

        Status newStatus = (Status) newEntity;

        setContent(newStatus.getContent());
        setCreateDate(newStatus.getCreateDate());
    }
}