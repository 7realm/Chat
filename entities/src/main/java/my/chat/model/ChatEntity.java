package my.chat.model;

import static my.chat.commons.ArgumentHelper.checkNotNull;

import java.io.Serializable;
import java.util.List;

public abstract class ChatEntity implements Serializable, Transferable<ChatEntity> {
    private static final long serialVersionUID = 691851149422851559L;

    /**
     * Update entity from entity of same class.
     * 
     * @param newEntity
     */
    public void update(ChatEntity newEntity) throws UpdateChatException {
        checkNotNull("newEntity", newEntity);

        if (!getClass().equals(newEntity.getClass())) {
            throw new UpdateChatException("Incorrect class %1 is set for update, %2 expected.", newEntity.getClass().getName(),
                getClass().getName());
        }
    }

    @Override
    public ChatEntity createTransferObject() throws UpdateChatException {
        try {
            ChatEntity result = getClass().newInstance();
            result.update(this);
            return result;
        } catch (ExceptionInInitializerError  e) {
            throw new UpdateChatException("Initialiation failed for class %1.", e, getClass().getName());
        } catch (InstantiationException e) {
            throw new UpdateChatException("Failed to create class or constructor of class %1.", e, getClass().getName());
        } catch (IllegalAccessException e) {
            throw new UpdateChatException("Failed to access class or constructor of class %1.", e, getClass().getName());
        }
    }
    
    @SuppressWarnings("unchecked")
    protected static <T extends ChatEntity> void makeTransferableList(List<T> list) throws UpdateChatException {
        for (int i = 0; i < list.size(); i++) {
            list.set(i, (T) list.get(i).createTransferObject());
        }
    }
}
