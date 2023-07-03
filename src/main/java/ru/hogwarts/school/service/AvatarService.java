package ru.hogwarts.school.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.exceptions.AvatarNotFoundException;
import ru.hogwarts.school.exceptions.FailedUploadFileException;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.repository.AvatarRepository;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class AvatarService {
    @Value("${upload.dir}")
    private String UPLOAD_DIR;
    private final AvatarRepository avatarRepository;

    public String saveAvatar(final MultipartFile file) {
        try {
            String fileName = file.getOriginalFilename();
            String filePath = UPLOAD_DIR + fileName;
            File avatarFile = new File(filePath);
            file.transferTo(avatarFile);
            byte[] fileBytes = Files.readAllBytes(avatarFile.toPath());

            Avatar avatar = new Avatar();
            avatar.setFilePath(filePath);
            avatar.setFileSize(file.getSize());
            avatar.setMediaType(file.getContentType());
            avatar.setData(fileBytes);

            avatarRepository.save(avatar);
            return "Загрузка Аватара прошла успешно";
        } catch (IOException e) {
            throw new FailedUploadFileException();
        }
    }

    public ResponseEntity<byte[]> getAvatarById(final Long id) {
        Avatar avatar = avatarRepository.findById(id)
                .orElseThrow(() -> new AvatarNotFoundException("Отсутствует Аватар по данному ИД"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(avatar.getMediaType()));
        return new ResponseEntity<>(avatar.getData(), headers, HttpStatus.OK);
    }

    public ResponseEntity<Resource> getAvatarFromFile(final String fileName) {
        File file = new File(UPLOAD_DIR + fileName + ".png");
        if (file.exists()) {
            Path path = Paths.get(file.getPath());
            UrlResource resource;
            try {
                resource = new UrlResource(path.toUri());
            } catch (MalformedURLException e) {
                throw new RuntimeException("Неправильный путь к файлу");
            }

            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_PNG)
                        .body(resource);
            }
        }
        throw new AvatarNotFoundException("Не найден Аватар по данному пути");
    }

    private byte[] generateImagePreview(String filePath) {
        try (InputStream is = Files.newInputStream(Path.of(filePath));
             BufferedInputStream bis = new BufferedInputStream(is, 1024);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            BufferedImage image = ImageIO.read(bis);

            int height = image.getHeight() / (image.getWidth() / 100);
            BufferedImage preview = new BufferedImage(100, height, image.getType());
            Graphics2D graphics = preview.createGraphics();
            graphics.drawImage(image, 0, 0, 100, height, null);
            graphics.dispose();

            ImageIO.write(preview, "png", baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
