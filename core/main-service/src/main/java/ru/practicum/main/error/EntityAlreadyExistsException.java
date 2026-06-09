package ru.practicum.main.error;

public class EntityAlreadyExistsException extends RuntimeException {

    public EntityAlreadyExistsException(String message) {
        super(message);
    }

    public EntityAlreadyExistsException(String entityName, String fieldName, String value) {
        super(String.format("%s c %s = '%s' уже существует", entityName, fieldName, value));
    }
}
