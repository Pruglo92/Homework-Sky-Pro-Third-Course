package ru.hogwarts.school.exceptions;

public class EmptyStringException extends RuntimeException {
    public EmptyStringException(String message) {
        super(message);
    }
}
