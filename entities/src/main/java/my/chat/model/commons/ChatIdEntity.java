package my.chat.model.commons;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class ChatIdEntity implements ChatEntity {
    private static final long serialVersionUID = -3813434674711319204L;

    @Id
    private long id;

    protected ChatIdEntity() {
        // empty
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    /**
     * Override this method,
     * 
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        // check classes
        if (!getClass().equals(obj.getClass())) {
            return false;
        }

        // check id values
        return id == ((ChatIdEntity) obj).getId();
    }

    @Override
    public int hashCode() {
        return (int) (super.hashCode() + id);
    }
}
