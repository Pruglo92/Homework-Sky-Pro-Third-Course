package ru.hogwarts.school.exceptions;

public class WrongStudentAgeException extends RuntimeException {
    public WrongStudentAgeException(String message) {
        super(message);
    }
}
