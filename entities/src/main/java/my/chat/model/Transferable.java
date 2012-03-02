package my.chat.model;

public interface Transferable<T extends ChatEntity> {
    T createTransferObject() throws UpdateChatException;
}
