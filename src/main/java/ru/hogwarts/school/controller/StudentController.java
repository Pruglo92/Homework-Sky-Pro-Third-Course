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
    public ResponseEntity<Student> addStudent(@RequestParam String name,
                                              @RequestParam int age) {
        return ResponseEntity.ok(studentService.addStudent(name, age));
    }

    @Operation(summary = "Получить студента по ИД")
    @GetMapping("/get/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getStudentById(id));
    }

    @Operation(summary = "Изменить студента")
    @PutMapping("/put")
    public ResponseEntity<Student> changeStudent(@RequestParam Long id,
                                                 @RequestParam String name,
                                                 @RequestParam int age) {
        return ResponseEntity.ok(studentService.changeStudentById(id, name, age));
    }

    @Operation(summary = "Удалить студента")
    @DeleteMapping("/remove/{id}")
    public ResponseEntity<Student> removeStudentById(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.removeStudentById(id));
    }

    @Operation(summary = "Получить всх студентов по возрасту")
    @GetMapping("/all/{age}")
    public ResponseEntity<List<Student>> getStudentsByAge(@PathVariable int age) {
        return ResponseEntity.ok(studentService.getStudentsByAge(age));
    }
}
