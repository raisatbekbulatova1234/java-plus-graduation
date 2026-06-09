package ru.practicum.main.error;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String entityName, String fieldName, Object value) {
        super(String.format("%s с %s = '%s' не найден", entityName, fieldName, value));
    }
}