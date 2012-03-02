package my.chat.knowledge;

import my.chat.model.ChatIdEntity;

public interface KnowledgeService {

    /**
     * Get entity by id and class.
     * 
     * @param id
     * @param clazz
     * @return
     * @throws KnowledgeChatException 
     */
    <T extends ChatIdEntity> T get(long id, Class<T> clazz) throws KnowledgeChatException;

    /**
     * Update existing entity or add new.
     * 
     * @param entity
     */
    <T extends ChatIdEntity> void update(T entity) throws KnowledgeChatException;
}
