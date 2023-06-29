package ru.hogwarts.school.exceptions;

public class AvatarNotFoundException extends RuntimeException {
    public AvatarNotFoundException(String message) {
        super(message);
    }
}
