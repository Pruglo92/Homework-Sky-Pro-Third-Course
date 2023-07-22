package ru.hogwarts.school.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.service.AvatarService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AvatarController.class)
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class AvatarControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AvatarService avatarService;

    @Test
    @DisplayName("Тест на загрузке аватара")
    public void testUploadAvatar() throws Exception {
        String fileName = "avatar.jpg";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                fileName,
                MediaType.IMAGE_JPEG_VALUE,
                "test data".getBytes()
        );

        String avatarPath = "/avatars/" + fileName;
        when(avatarService.saveAvatar(file)).thenReturn(avatarPath);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/avatars/upload")
                        .file(file))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(avatarPath));
    }

    @Test
    @DisplayName("Тест на получение аватара из БД")
    public void testGetAvatarById() throws Exception {
        Long avatarId = 1L;
        Avatar avatar = createAvatar();
        when(avatarService.getAvatarById(avatarId)).thenReturn(ResponseEntity.ok(avatar.getData()));

        mockMvc.perform(MockMvcRequestBuilders.get("/avatars/{id}", avatarId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().bytes(avatar.getData()));
    }

    @Test
    @DisplayName("Тест на получение аватара из директории")
    public void testGetAvatarFromFile() throws Exception {
        String fileName = "avatar.jpg";
        Avatar avatar = createAvatar();
        when(avatarService.getAvatarFromFile(fileName)).thenReturn(ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(avatar.getMediaType()))
                .body(new ByteArrayResource(avatar.getData())));

        mockMvc.perform(MockMvcRequestBuilders.get("/avatars/files/{fileName}", fileName))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().bytes(avatar.getData()));
    }

    @Test
    @DisplayName("Тест на получение всех аватаров")
    public void testGetAllAvatar() throws Exception {
        int pageNumber = 0;
        int pageSize = 10;
        List<Avatar> avatars = createAvatarList();

        when(avatarService.getAllAvatar(pageNumber, pageSize)).thenReturn(avatars);

        mockMvc.perform(MockMvcRequestBuilders.get("/avatars/all")
                        .param("page", String.valueOf(pageNumber))
                        .param("size", String.valueOf(pageSize)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(avatars.size()));
    }

    private Avatar createAvatar() {
        return new Avatar("test.jpg",
                1024L,
                MediaType.IMAGE_JPEG_VALUE,
                "test data".getBytes(),
                null);
    }

    private List<Avatar> createAvatarList() {
        List<Avatar> avatars = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Avatar avatar = createAvatar();
            avatar.setId((long) i + 1);
            avatars.add(avatar);
        }
        return avatars;
    }
}
