package ru.hogwarts.school.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.service.FacultyService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/faculty")
public class FacultyController {
    private final FacultyService facultyService;

    @Operation(summary = "Добавить факультет")
    @PostMapping("/add")
    public ResponseEntity<Faculty> addFaculty(@RequestParam String name,
                                              @RequestParam String color) {
        return ResponseEntity.ok(facultyService.addFaculty(name, color));
    }

    @Operation(summary = "Получить факультет по ИД")
    @GetMapping("/get/{id}")
    public ResponseEntity<Faculty> getFacultyById(@PathVariable Long id) {
        return ResponseEntity.ok(facultyService.getFacultyById(id));
    }

    @Operation(summary = "Изменить факультет")
    @PutMapping("/put")
    public ResponseEntity<Faculty> changeFaculty(@RequestParam Long id,
                                                 @RequestParam String name,
                                                 @RequestParam String color) {
        return ResponseEntity.ok(facultyService.changeFacultyById(id, name, color));
    }

    @Operation(summary = "Удалить факультет")
    @DeleteMapping("/remove/{id}")
    public ResponseEntity<Faculty> removeFacultyById(@PathVariable Long id) {
        return ResponseEntity.ok(facultyService.removeFacultyById(id));
    }

    @Operation(summary = "Получить все факультеты по цвету")
    @GetMapping("/all/{color}")
    public ResponseEntity<List<Faculty>> getFacultiesByAge(@PathVariable String color) {
        return ResponseEntity.ok(facultyService.getFacultiesByAge(color));
    }
}
