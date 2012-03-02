package my.chat.knowledge.impl;

import static my.chat.commons.ArgumentHelper.checkNotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import my.chat.knowledge.KnowledgeChatException;
import my.chat.knowledge.KnowledgeService;
import my.chat.model.ChatIdEntity;
import my.chat.model.UpdateChatException;

public class KnowledgeServiceImpl implements KnowledgeService {

    /** Internal data map, that will be used */
    private Map<KnowledgeKey, ChatIdEntity> data = new HashMap<KnowledgeKey, ChatIdEntity>();

    /** Lock, used to handle multiple read and writes. */
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    @Override
    public <T extends ChatIdEntity> T get(long id, Class<T> clazz) throws KnowledgeChatException {
        checkNotNull("clazz", clazz);

        lock.readLock().lock();
        try {
            return clazz.cast(data.get(new KnowledgeKey(id, clazz)));
        } catch (ClassCastException e) {
            throw new KnowledgeChatException("Failed to get object with id %1 of type %2.", id, clazz.getName());
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public <T extends ChatIdEntity> void update(T entity) throws KnowledgeChatException {
        checkNotNull("entity", entity);

        lock.writeLock().lock();
        try {
            KnowledgeKey key = new KnowledgeKey(entity.getId(), entity.getClass());

            if (data.containsKey(key)) {
                // update if this key is present
                ChatIdEntity oldEntity = data.get(key);
                oldEntity.update(entity);
            } else {
                // create new entity
                data.put(key, entity);
            }
        } catch (UpdateChatException e) {
            throw new KnowledgeChatException("Failed to update entity %1.", e, entity.getClass().getName());
        } finally {
            lock.writeLock().unlock();
        }
    }
}
