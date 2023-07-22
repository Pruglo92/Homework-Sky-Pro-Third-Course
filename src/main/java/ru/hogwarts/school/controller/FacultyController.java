package ru.hogwarts.school.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.dto.FacultyRequestDto;
import ru.hogwarts.school.dto.FacultyResponseDto;
import ru.hogwarts.school.service.FacultyService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/faculty")
public class FacultyController {
    private final FacultyService facultyService;

    @Operation(summary = "Добавить факультет")
    @PostMapping("/add")
    public ResponseEntity<FacultyResponseDto> addFaculty(@RequestBody FacultyRequestDto dto) {
        return ResponseEntity.ok(facultyService.addFaculty(dto));
    }

    @Operation(summary = "Получить факультет по ИД")
    @GetMapping("/get/{id}")
    public ResponseEntity<FacultyResponseDto> getFacultyById(@PathVariable Long id) {
        return ResponseEntity.ok(facultyService.getFacultyById(id));
    }

    @Operation(summary = "Изменить факультет")
    @PutMapping("/put/{id}")
    public ResponseEntity<FacultyResponseDto> changeFaculty(@PathVariable Long id,
                                                            @RequestBody FacultyRequestDto dto) {
        return ResponseEntity.ok(facultyService.changeFaculty(id, dto));
    }

    @Operation(summary = "Удалить факультет")
    @DeleteMapping("/remove/{id}")
    public ResponseEntity<String> removeFacultyById(@PathVariable Long id) {
        facultyService.removeFacultyById(id);
        return ResponseEntity.ok("Факультет успешно удалён");
    }

    @Operation(summary = "Получить все факультеты по цвету")
    @GetMapping("/all/{color}")
    public ResponseEntity<List<FacultyResponseDto>> getFacultiesByColor(@PathVariable String color) {
        return ResponseEntity.ok(facultyService.getFacultiesByColor(color));
    }

    @Operation(summary = "Получить факультет по цвету или имени")
    @GetMapping("/{colorOrName}")
    public ResponseEntity<FacultyResponseDto> getFacultiesByColorOrName(@PathVariable String colorOrName) {
        return ResponseEntity.ok(facultyService.getFacultyByColorOrName(colorOrName));
    }

    @Operation(summary = "Получить факультет по ИД или имени студента")
    @GetMapping()
    public ResponseEntity<FacultyResponseDto> getFacultyByStudentIdOrName(@RequestParam(required = false) Long id,
                                                                          @RequestParam(required = false) String name) {
        return ResponseEntity.ok(facultyService.getFacultyByStudentIdOrName(id, name));
    }

    @Operation(summary = "Получить самое длинное имя факультета")
    @GetMapping("/longest-name")
    public ResponseEntity<String> getLongestFacultyName() {
        return ResponseEntity.ok(facultyService.getLongestFacultyName());
    }
}
