package ru.hogwarts.school.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.dto.StudentRequestDto;
import ru.hogwarts.school.dto.StudentResponseDto;
import ru.hogwarts.school.service.StudentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/student")
public class StudentController {
    private final StudentService studentService;

    @Operation(summary = "Добавить студента")
    @PostMapping("/add")
    public ResponseEntity<StudentResponseDto> addStudent(@RequestBody StudentRequestDto dto) {
        return ResponseEntity.ok(studentService.addStudent(dto));
    }

    @Operation(summary = "Получить студента по ИД")
    @GetMapping("/get/{id}")
    public ResponseEntity<StudentResponseDto> getStudentById(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getStudentById(id));
    }

    @Operation(summary = "Изменить студента")
    @PutMapping("/put/{id}")
    public ResponseEntity<StudentResponseDto> changeStudent(@PathVariable Long id,
                                                            @RequestBody StudentRequestDto dto) {
        return ResponseEntity.ok(studentService.changeStudentById(id, dto));
    }

    @Operation(summary = "Удалить студента")
    @DeleteMapping("/remove/{id}")
    public ResponseEntity<String> removeStudentById(@PathVariable Long id) {
        studentService.removeStudentById(id);
        return ResponseEntity.ok("Студент успешно удалён");
    }

    @Operation(summary = "Получить всх студентов по возрасту")
    @GetMapping("/all/{age}")
    public ResponseEntity<List<StudentResponseDto>> getStudentsByAge(@PathVariable int age) {
        return ResponseEntity.ok(studentService.getStudentsByAge(age));
    }

    @Operation(summary = "Получить всх студентов в промежутке по возрасту")
    @GetMapping("/allBetween/{minAge}/{maxAge}")
    public ResponseEntity<List<StudentResponseDto>> getStudentsByAgeBetween(@PathVariable int minAge,
                                                                            @PathVariable int maxAge) {
        return ResponseEntity.ok(studentService.getStudentsByAgeBetween(minAge, maxAge));
    }

    @Operation(summary = "Получить всех студентов по ИД факультета")
    @GetMapping("/students/{id}")
    public ResponseEntity<List<StudentResponseDto>> getStudentsByFacultyId(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getStudentsByFacultyId(id));
    }

    @Operation(summary = "Получить количество всех студентов")
    @GetMapping("/students/count")
    public ResponseEntity<Integer> getStudentCount() {
        return ResponseEntity.ok(studentService.getStudentCount());
    }

    @Operation(summary = "Получить средний возраст студентов")
    @GetMapping("/students/average-age")
    public ResponseEntity<Integer> getAverageAge() {
        return ResponseEntity.ok(studentService.getAverageAge());
    }

    @Operation(summary = "Получить средний возраст студентов (stream)")
    @GetMapping("/students/average-age/stream")
    public ResponseEntity<String> getAverageAgeUsingStream() {
        return ResponseEntity.ok(studentService.getAverageAgeUsingStream());
    }

    @Operation(summary = "Получить последние 5 студентов")
    @GetMapping("/students/last-five")
    public ResponseEntity<List<StudentResponseDto>> getLastFiveStudents() {
        return ResponseEntity.ok(studentService.getLastFiveStudents());
    }

    @Operation(summary = "Получить имена студентов,начинающихся на букву \"А\"")
    @GetMapping("/names-starting-with-A")
    public ResponseEntity<List<String>> getStudentNamesStartingWithA() {
        return ResponseEntity.ok(studentService.getStudentNamesStartingWithA());
    }
}
