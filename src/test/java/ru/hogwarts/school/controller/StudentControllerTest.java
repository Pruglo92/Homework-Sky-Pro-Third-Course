package ru.hogwarts.school.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.hogwarts.school.dto.StudentRequestDto;
import ru.hogwarts.school.dto.StudentResponseDto;
import ru.hogwarts.school.service.StudentService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@ExtendWith(SpringExtension.class)
@WebMvcTest(StudentController.class)
public class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    private StudentResponseDto createStudentResponseDto() {
        return new StudentResponseDto(1L, "Василий", 25);
    }

    private void setupStudentServiceMock(StudentResponseDto student) {
        when(studentService.addStudent(any(StudentRequestDto.class))).thenReturn(student);
        when(studentService.getStudentById(anyLong())).thenReturn(student);
        when(studentService.changeStudentById(anyLong(), any(StudentRequestDto.class))).thenReturn(student);
        doNothing().when(studentService).removeStudentById(anyLong());
        when(studentService.getStudentsByAge(anyInt())).thenReturn(Collections.singletonList(student));
        when(studentService.getStudentsByAgeBetween(anyInt(), anyInt())).thenReturn(Collections.singletonList(student));
        when(studentService.getStudentsByFacultyId(anyLong())).thenReturn(Collections.singletonList(student));
        when(studentService.getStudentNamesStartingWithA()).thenReturn(Collections.singletonList(student.name()));
        when(studentService.getAverageAge()).thenReturn(25);
        when(studentService.getAverageAgeUsingStream()).thenReturn("25");
        when(studentService.getStudentCount()).thenReturn(10);
        when(studentService.getLastFiveStudents()).thenReturn(Arrays.asList(
                new StudentResponseDto(1L, "Василий", 25),
                new StudentResponseDto(2L, "Мария", 22),
                new StudentResponseDto(3L, "Иван", 23),
                new StudentResponseDto(4L, "Елена", 24),
                new StudentResponseDto(5L, "Алексей", 21)
        ));
    }

    @Test
    @DisplayName("Тест на добавление студента")
    public void testAddStudent() throws Exception {
        StudentResponseDto student = createStudentResponseDto();
        setupStudentServiceMock(student);

        mockMvc.perform(MockMvcRequestBuilders.post("/student/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(new StudentRequestDto(student.name(), student.age()))))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(student.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.age").value(student.age()));
    }

    @Test
    @DisplayName("Тест на получение студента по ИД")
    public void testGetStudentById() throws Exception {
        Long studentId = 1L;
        StudentResponseDto student = createStudentResponseDto();
        setupStudentServiceMock(student);

        mockMvc.perform(MockMvcRequestBuilders.get("/student/get/{id}", studentId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(student.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.age").value(student.age()));
    }

    @Test
    @DisplayName("Тест на изменение студента")
    public void testChangeStudent() throws Exception {
        Long studentId = 1L;
        StudentResponseDto student = createStudentResponseDto();
        setupStudentServiceMock(student);

        mockMvc.perform(MockMvcRequestBuilders.put("/student/put/{id}", studentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(new StudentRequestDto(student.name(), student.age()))))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(student.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.age").value(student.age()));
    }

    @Test
    @DisplayName("Тест на удаление студента по ИД")
    public void testRemoveStudentById() throws Exception {
        Long studentId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.delete("/student/remove/{id}", studentId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Студент успешно удалён"));
    }

    @Test
    @DisplayName("Тест на получение студентов по возрасту")
    public void testGetStudentsByAge() throws Exception {
        int age = 25;
        StudentResponseDto student = createStudentResponseDto();
        setupStudentServiceMock(student);

        mockMvc.perform(MockMvcRequestBuilders.get("/student/all/{age}", age))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(student.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].age").value(student.age()));
    }

    @Test
    @DisplayName("Тест на получение студентов в возрастном промежутке")
    public void testGetStudentsByAgeBetween() throws Exception {
        int minAge = 20;
        int maxAge = 30;
        StudentResponseDto student = createStudentResponseDto();
        setupStudentServiceMock(student);

        mockMvc.perform(MockMvcRequestBuilders.get("/student/allBetween/{minAge}/{maxAge}", minAge, maxAge))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(student.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].age").value(student.age()));
    }

    @Test
    @DisplayName("Тест на получение студентов по ИД факультета")
    public void testGetStudentsByFacultyId() throws Exception {
        Long facultyId = 1L;
        StudentResponseDto student = createStudentResponseDto();
        setupStudentServiceMock(student);

        mockMvc.perform(MockMvcRequestBuilders.get("/student/students/{id}", facultyId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(student.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].age").value(student.age()));
    }

    @Test
    @DisplayName("Тест на получение имен студентов, чье имя начинается с буквы А")
    public void testGetStudentNamesStartingWithA() throws Exception {
        StudentResponseDto student = createStudentResponseDto();
        setupStudentServiceMock(student);

        mockMvc.perform(MockMvcRequestBuilders.get("/student/names-starting-with-A"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0]").value(student.name()));
    }

    @Test
    @DisplayName("Тест на получение сруднего возраста студентов(запрос с БД)")
    public void testGetAverageAge() throws Exception {
        int averageAge = 25;
        setupStudentServiceMock(createStudentResponseDto());

        mockMvc.perform(MockMvcRequestBuilders.get("/student/students/average-age"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(String.valueOf(averageAge)));
    }

    @Test
    @DisplayName("Тест на получение сруднего возраста студентов(через stream)")
    public void testGetAverageAgeUsingStream() throws Exception {
        String averageAge = "25";
        setupStudentServiceMock(createStudentResponseDto());

        mockMvc.perform(MockMvcRequestBuilders.get("/student/students/average-age/stream"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(averageAge));
    }

    @Test
    @DisplayName("Тест на получение колличества студентов")
    public void testGetStudentCount() throws Exception {
        int studentCount = 10;
        setupStudentServiceMock(createStudentResponseDto());

        mockMvc.perform(MockMvcRequestBuilders.get("/student/students/count"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(String.valueOf(studentCount)));
    }

    @Test
    @DisplayName("Тест на получение последних пяти студентов")
    public void testGetLastFiveStudents() throws Exception {
        List<StudentResponseDto> students = Arrays.asList(
                new StudentResponseDto(1L, "Василий", 25),
                new StudentResponseDto(2L, "Мария", 22),
                new StudentResponseDto(3L, "Иван", 23),
                new StudentResponseDto(4L, "Елена", 24),
                new StudentResponseDto(5L, "Алексей", 21)
        );
        setupStudentServiceMock(createStudentResponseDto());

        mockMvc.perform(MockMvcRequestBuilders.get("/student/students/last-five"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(students.get(0).name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].age").value(students.get(0).age()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value(students.get(1).name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].age").value(students.get(1).age()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].name").value(students.get(2).name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].age").value(students.get(2).age()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[3].name").value(students.get(3).name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[3].age").value(students.get(3).age()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[4].name").value(students.get(4).name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[4].age").value(students.get(4).age()));
    }

    private static String asJsonString(Object obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(obj);
    }
}