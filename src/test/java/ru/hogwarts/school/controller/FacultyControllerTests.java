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
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.service.FacultyService;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebMvcTest(FacultyController.class)
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class FacultyControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FacultyService facultyService;

    private Faculty createFaculty() {
        Faculty faculty = new Faculty();
        faculty.setName("Гриффиндор");
        faculty.setColor("Красный");
        return faculty;
    }

    private void setupFacultyServiceMock(Faculty faculty) {
        when(facultyService.addFaculty(any(Faculty.class))).thenReturn(faculty);
        when(facultyService.getFacultyById(anyLong())).thenReturn(faculty);
        when(facultyService.changeFaculty(any(Faculty.class))).thenReturn(faculty);
        doNothing().when(facultyService).removeFacultyById(anyLong());
        when(facultyService.getFacultiesByColor(anyString())).thenReturn(Collections.singletonList(faculty));
        when(facultyService.getFacultyByColorOrName(anyString())).thenReturn(faculty);
        when(facultyService.getFacultyByStudentIdOrName(anyLong(), anyString())).thenReturn(faculty);
    }

    @Test
    public void testAddFaculty() throws Exception {
        Faculty faculty = createFaculty();
        setupFacultyServiceMock(faculty);

        mockMvc.perform(MockMvcRequestBuilders.post("/faculty/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(faculty)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(faculty.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.color").value(faculty.getColor()));
    }

    @Test
    public void testGetFacultyById() throws Exception {
        Long facultyId = 1L;
        Faculty faculty = createFaculty();
        setupFacultyServiceMock(faculty);

        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/get/{id}", facultyId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(faculty.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.color").value(faculty.getColor()));
    }

    @Test
    public void testChangeFaculty() throws Exception {
        Faculty faculty = createFaculty();
        setupFacultyServiceMock(faculty);

        mockMvc.perform(MockMvcRequestBuilders.put("/faculty/put")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(faculty)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(faculty.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.color").value(faculty.getColor()));
    }

    @Test
    public void testRemoveFacultyById() throws Exception {
        Long facultyId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.delete("/faculty/remove/{id}", facultyId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Факультет успешно удалён"));
    }

    @Test
    public void testGetFacultiesByColor() throws Exception {
        Faculty faculty = createFaculty();
        setupFacultyServiceMock(faculty);

        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/all/{color}", faculty.getColor()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(faculty.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].color").value(faculty.getColor()));
    }

    @Test
    public void testGetFacultiesByColorOrName() throws Exception {
        Faculty faculty = createFaculty();
        setupFacultyServiceMock(faculty);

        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/{colorOrName}", faculty.getColor()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(faculty.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.color").value(faculty.getColor()));
    }

    @Test
    public void testGetFacultyByStudentIdOrName() throws Exception {
        Long studentId = 1L;
        String studentName = "Василий";
        Faculty faculty = createFaculty();
        setupFacultyServiceMock(faculty);

        mockMvc.perform(MockMvcRequestBuilders.get("/faculty")
                        .param("id", studentId.toString())
                        .param("name", studentName))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(faculty.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.color").value(faculty.getColor()));
    }

    private static String asJsonString(Object obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(obj);
    }
}
