package my.chat.knowledge.impl;


public class KnowledgeKey {

    private long id;

    private Class<?> clazz;

    /**
     * @param id
     * @param clazz
     */
    public KnowledgeKey(long id, Class<?> clazz) {
        super();
        this.id = id;
        this.clazz = clazz;
    }

    public long getId() {
        return id;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof KnowledgeKey) {
            KnowledgeKey keyToCheck = (KnowledgeKey) obj;
            return clazz.equals(keyToCheck.getClazz()) && id == keyToCheck.getId();
        } else {
            return false;
        }
    }

}
