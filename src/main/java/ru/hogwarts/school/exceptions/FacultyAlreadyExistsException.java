package ru.hogwarts.school.exceptions;

public class FacultyAlreadyExistsException extends RuntimeException {
    public FacultyAlreadyExistsException(String message) {
        super(message);
    }
}
