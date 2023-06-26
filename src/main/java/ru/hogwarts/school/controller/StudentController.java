package ru.hogwarts.school.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/student")
public class StudentController {
    private final StudentService studentService;

    @Operation(summary = "Добавить студента")
    @PostMapping("/add")
    public ResponseEntity<Student> addStudent(@RequestBody Student student) {
        return ResponseEntity.ok(studentService.addStudent(student));
    }

    @Operation(summary = "Получить студента по ИД")
    @GetMapping("/get/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getStudentById(id));
    }

    @Operation(summary = "Изменить студента")
    @PutMapping("/put")
    public ResponseEntity<Student> changeStudent(@RequestBody Student student) {
        return ResponseEntity.ok(studentService.changeStudentById(student));
    }

    @Operation(summary = "Удалить студента")
    @DeleteMapping("/remove/{id}")
    public ResponseEntity<String> removeStudentById(@PathVariable Long id) {
        studentService.removeStudentById(id);
        return ResponseEntity.ok("Студент успешно удалён");
    }

    @Operation(summary = "Получить всх студентов по возрасту")
    @GetMapping("/all/{age}")
    public ResponseEntity<List<Student>> getStudentsByAge(@PathVariable int age) {
        return ResponseEntity.ok(studentService.getStudentsByAge(age));
    }
}
