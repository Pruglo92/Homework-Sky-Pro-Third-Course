package ru.hogwarts.school.exceptions.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.hogwarts.school.exceptions.FacultyNotFoundException;
import ru.hogwarts.school.exceptions.FailedUploadFileException;
import ru.hogwarts.school.exceptions.InvalidFacultySearchException;
import ru.hogwarts.school.exceptions.StudentNotFoundException;

@ControllerAdvice
public class Handler {

    @ExceptionHandler
    public <T extends Exception> ResponseEntity<String> handleException(T e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String message = e.getMessage();

        if (e instanceof FacultyNotFoundException || e instanceof StudentNotFoundException) {
            status = HttpStatus.NOT_FOUND;
        } else if (e instanceof InvalidFacultySearchException) {
            status = HttpStatus.UNPROCESSABLE_ENTITY;
        } else if (e instanceof FailedUploadFileException) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            message = "Ошибка загрузки файла";
        }

        return ResponseEntity.status(status).body(message);
    }
}