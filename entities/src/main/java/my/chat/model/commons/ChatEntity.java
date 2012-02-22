package my.chat.model.commons;

import static my.chat.commons.ArgumentHelper.checkNotNull;

import java.io.Serializable;

public abstract class ChatEntity implements Serializable {
    private static final long serialVersionUID = 691851149422851559L;

    /**
     * Update entity from entity of same class.
     * 
     * @param newEntity
     */
    public void update(ChatEntity newEntity) throws UpdateChatException {
        checkNotNull("newEntity", newEntity);
        
        if (!getClass().equals(newEntity.getClass())) {
            throw new UpdateChatException("Incorrect class %1 is set for update, %2 expected.", newEntity.getClass().getName(), getClass().getName());
        }
    }
}
