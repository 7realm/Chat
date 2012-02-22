package my.chat.knowledge;

import my.chat.model.commons.ChatIdEntity;

public interface KnowledgeService {

    /**
     * Get entity by id and class.
     * 
     * @param id
     * @param clazz
     * @return
     */
    <T extends ChatIdEntity> T get(long id, Class<T> clazz);

    /**
     * Update existing entity or add new.
     * 
     * @param entity
     */
    void update(ChatIdEntity entity);
}
