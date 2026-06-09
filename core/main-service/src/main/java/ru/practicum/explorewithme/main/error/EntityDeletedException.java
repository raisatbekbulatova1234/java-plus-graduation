package ru.practicum.explorewithme.main.error;

public class EntityDeletedException  extends RuntimeException {

    public EntityDeletedException(String message) {
        super(message);
    }

    public EntityDeletedException(String entityName, String fieldName, Object value) {
        super(String.format("Ограничение на удаление %s с %s = '%s' - не пустая", entityName, fieldName, value));
    }
}
