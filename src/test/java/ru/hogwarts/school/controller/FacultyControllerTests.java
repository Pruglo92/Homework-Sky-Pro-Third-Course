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
import ru.hogwarts.school.dto.FacultyRequestDto;
import ru.hogwarts.school.dto.FacultyResponseDto;
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

    private FacultyResponseDto createFacultyResponseDto() {
        return new FacultyResponseDto(1L, "Гриффиндор", "Красный");
    }

    private void setupFacultyServiceMock(FacultyResponseDto faculty) {
        when(facultyService.addFaculty(any(FacultyRequestDto.class))).thenReturn(faculty);
        when(facultyService.getFacultyById(anyLong())).thenReturn(faculty);
        when(facultyService.changeFaculty(anyLong(), any(FacultyRequestDto.class))).thenReturn(faculty);
        doNothing().when(facultyService).removeFacultyById(anyLong());
        when(facultyService.getFacultiesByColor(anyString())).thenReturn(Collections.singletonList(faculty));
        when(facultyService.getFacultyByColorOrName(anyString())).thenReturn(faculty);
        when(facultyService.getFacultyByStudentIdOrName(anyLong(), anyString())).thenReturn(faculty);
        when(facultyService.getLongestFacultyName()).thenReturn("Гриффиндор");
    }

    @Test
    @DisplayName("Тест на добавление факультета")
    public void testAddFaculty() throws Exception {
        FacultyResponseDto faculty = createFacultyResponseDto();
        setupFacultyServiceMock(faculty);

        mockMvc.perform(MockMvcRequestBuilders.post("/faculty/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(new FacultyRequestDto(faculty.name(), faculty.color()))))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(faculty.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.color").value(faculty.color()));
    }

    @Test
    @DisplayName("Тест на получение факультета по ИД")
    public void testGetFacultyById() throws Exception {
        Long facultyId = 1L;
        FacultyResponseDto faculty = createFacultyResponseDto();
        setupFacultyServiceMock(faculty);

        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/get/{id}", facultyId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(faculty.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.color").value(faculty.color()));
    }

    @Test
    @DisplayName("Тест на изменение факультета")
    public void testChangeFaculty() throws Exception {
        Long facultyId = 1L;
        FacultyResponseDto faculty = createFacultyResponseDto();
        setupFacultyServiceMock(faculty);

        mockMvc.perform(MockMvcRequestBuilders.put("/faculty/put/{id}", facultyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(new FacultyRequestDto(faculty.name(), faculty.color()))))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(faculty.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.color").value(faculty.color()));
    }

    @Test
    @DisplayName("Тест на удаление факультета по ИД")
    public void testRemoveFacultyById() throws Exception {
        Long facultyId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.delete("/faculty/remove/{id}", facultyId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Факультет успешно удалён"));
    }

    @Test
    @DisplayName("Тест на получение факультета по цвету")
    public void testGetFacultiesByColor() throws Exception {
        FacultyResponseDto faculty = createFacultyResponseDto();
        setupFacultyServiceMock(faculty);

        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/all/{color}", faculty.color()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(faculty.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].color").value(faculty.color()));
    }

    @Test
    @DisplayName("Тест на получение факультета по цвету или имени")
    public void testGetFacultiesByColorOrName() throws Exception {
        FacultyResponseDto faculty = createFacultyResponseDto();
        setupFacultyServiceMock(faculty);

        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/{colorOrName}", faculty.color()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(faculty.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.color").value(faculty.color()));
    }

    @Test
    @DisplayName("Тест на получение факультета по ИД или имени студента")
    public void testGetFacultyByStudentIdOrName() throws Exception {
        Long studentId = 1L;
        String studentName = "Василий";
        FacultyResponseDto faculty = createFacultyResponseDto();
        setupFacultyServiceMock(faculty);

        mockMvc.perform(MockMvcRequestBuilders.get("/faculty")
                        .param("id", studentId.toString())
                        .param("name", studentName))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(faculty.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.color").value(faculty.color()));
    }

    @Test
    @DisplayName("Тест на получение самого длинного имени факультета")
    public void testGetLongestFacultyName() throws Exception {
        String longestFacultyName = "Гриффиндор";
        setupFacultyServiceMock(createFacultyResponseDto());

        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/longest-name"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(longestFacultyName));
    }

    private static String asJsonString(Object obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(obj);
    }
}
