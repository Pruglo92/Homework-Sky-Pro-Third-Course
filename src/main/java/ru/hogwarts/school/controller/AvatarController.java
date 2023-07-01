package ru.hogwarts.school.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.service.AvatarService;

@RestController
@RequestMapping("/avatars")
@RequiredArgsConstructor
public class AvatarController {

    private final AvatarService avatarService;

    @PostMapping(
            path = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadAvatar(
            @RequestPart("file") MultipartFile file) {
        return ResponseEntity.ok(avatarService.saveAvatar(file));
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getAvatarById(@PathVariable Long id) {
        return avatarService.getAvatarById(id);
    }

    @GetMapping("/files/{fileName}")
    public ResponseEntity<Resource> getAvatarFromFile(@PathVariable String fileName) {
        return avatarService.getAvatarFromFile(fileName);
    }
}
