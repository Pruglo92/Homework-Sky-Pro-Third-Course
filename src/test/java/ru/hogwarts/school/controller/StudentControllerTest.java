package ru.hogwarts.school.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebMvcTest(StudentController.class)
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    private Student createStudent() {
        Student student = new Student();
        student.setName("Василий");
        student.setAge(25);
        return student;
    }

    private void setupStudentServiceMock(Student student) {
        when(studentService.addStudent(any(Student.class))).thenReturn(student);
        when(studentService.getStudentById(anyLong())).thenReturn(student);
        when(studentService.changeStudentById(any(Student.class))).thenReturn(student);
        doNothing().when(studentService).removeStudentById(anyLong());
        when(studentService.getStudentsByAge(anyInt())).thenReturn(Collections.singletonList(student));
        when(studentService.getStudentsByAgeBetween(anyInt(), anyInt())).thenReturn(Collections.singletonList(student));
        when(studentService.getStudentsByFacultyId(anyLong())).thenReturn(Collections.singletonList(student));
    }

    @Test
    public void testAddStudent() throws Exception {
        Student student = createStudent();
        setupStudentServiceMock(student);

        mockMvc.perform(MockMvcRequestBuilders.post("/student/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(student)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(student.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.age").value(student.getAge()));
    }

    @Test
    public void testGetStudentById() throws Exception {
        Long studentId = 1L;
        Student student = createStudent();
        setupStudentServiceMock(student);

        mockMvc.perform(MockMvcRequestBuilders.get("/student/get/{id}", studentId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(student.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.age").value(student.getAge()));
    }

    @Test
    public void testChangeStudent() throws Exception {
        Student student = createStudent();
        setupStudentServiceMock(student);

        mockMvc.perform(MockMvcRequestBuilders.put("/student/put")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(student)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(student.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.age").value(student.getAge()));
    }

    @Test
    public void testRemoveStudentById() throws Exception {
        Long studentId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.delete("/student/remove/{id}", studentId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Студент успешно удалён"));
    }

    @Test
    public void testGetStudentsByAge() throws Exception {
        int age = 20;
        Student student = createStudent();
        setupStudentServiceMock(student);

        mockMvc.perform(MockMvcRequestBuilders.get("/student/all/{age}", age))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(student.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].age").value(student.getAge()));
    }

    @Test
    public void testGetStudentsByAgeBetween() throws Exception {
        int minAge = 10;
        int maxAge = 20;
        Student student = createStudent();
        setupStudentServiceMock(student);

        mockMvc.perform(MockMvcRequestBuilders.get("/student/allBetween/{minAge}/{maxAge}", minAge, maxAge))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(student.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].age").value(student.getAge()));
    }

    @Test
    public void testGetStudentsByFacultyId() throws Exception {
        Long facultyId = 1L;
        Student student = createStudent();
        setupStudentServiceMock(student);

        mockMvc.perform(MockMvcRequestBuilders.get("/student/students/{id}", facultyId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(student.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].age").value(student.getAge()));
    }

    private static String asJsonString(Object obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(obj);
    }
}
