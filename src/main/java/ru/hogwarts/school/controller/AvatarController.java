package ru.hogwarts.school.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.service.AvatarService;

import java.util.List;

@RestController
@RequestMapping("/avatars")
@RequiredArgsConstructor
public class AvatarController {

    private final AvatarService avatarService;

    @Operation(summary = "Загрузить аватар")
    @PostMapping(
            path = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadAvatar(
            @RequestPart("file") MultipartFile file) {
        return ResponseEntity.ok(avatarService.saveAvatar(file));
    }

    @Operation(summary = "Скачать аватар из БД")
    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getAvatarById(@PathVariable Long id) {
        return avatarService.getAvatarById(id);
    }

    @Operation(summary = "Скачать аватар из директории")
    @GetMapping("/files/{fileName}")
    public ResponseEntity<Resource> getAvatarFromFile(@PathVariable String fileName) {
        return avatarService.getAvatarFromFile(fileName);
    }

    @Operation(summary = "Получить все аватары с пагинацией")
    @GetMapping("/all")
    public ResponseEntity<List<Avatar>> getAllAvatar(@RequestParam("page") Integer pageNumber,
                                                     @RequestParam("size") Integer pageSize) {
        return ResponseEntity.ok(avatarService.getAllAvatar(pageNumber, pageSize));
    }
}
