package ru.hogwarts.school.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AvatarService {
    @Value("${upload.dir}")
    private String UPLOAD_DIR;
    private final AvatarRepository avatarRepository;

    public String saveAvatar(final MultipartFile file) {
        log.info("Was invoked method for : {}", getMethodName());
        try {
            String fileName = file.getOriginalFilename();
            String filePath = UPLOAD_DIR + fileName;
            File avatarFile = new File(filePath);
            file.transferTo(avatarFile);
            log.debug("Avatar uploaded to directory");
            byte[] fileBytes = Files.readAllBytes(avatarFile.toPath());

            Avatar avatar = new Avatar();
            avatar.setFilePath(filePath);
            avatar.setFileSize(file.getSize());
            avatar.setMediaType(file.getContentType());
            avatar.setData(fileBytes);
            log.debug("Saving avatar : {}", fileName);

            try {
                avatarRepository.save(avatar);
            } catch (Exception e) {
                log.error("Failed to save avatar", e);
                throw e;
            }
            return "Загрузка Аватара прошла успешно";
        } catch (IOException e) {
            log.error("Failed to upload avatar", e);
            throw new FailedUploadFileException();
        }
    }

    public ResponseEntity<byte[]> getAvatarById(final Long id) {
        log.info("Was invoked method for : {}", getMethodName());
        log.debug("Fetching avatar with ID : {}", id);

        Avatar avatar = avatarRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Avatar not found with ID : {}", id);
                    return new AvatarNotFoundException("Отсутствует аватар c данным ИД");
                });

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(avatar.getMediaType()));
        return new ResponseEntity<>(avatar.getData(), headers, HttpStatus.OK);
    }

    public ResponseEntity<Resource> getAvatarFromFile(final String fileName) {
        log.info("Was invoked method for : {}", getMethodName());
        File file = new File(UPLOAD_DIR + fileName + ".png");
        if (file.exists()) {
            Path path = Paths.get(file.getPath());
            UrlResource resource;
            try {
                resource = new UrlResource(path.toUri());
            } catch (MalformedURLException e) {
                log.error("Wrong file path : {}", path);
                throw new RuntimeException("Неправильный путь к файлу");
            }

            if (resource.exists()) {
                log.debug("Avatar resource found for path: {}", file.getPath());
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_PNG)
                        .body(resource);
            }
        }
        log.error("No avatar found for this path : {}", file.getPath());
        throw new AvatarNotFoundException("Не найден Аватар по данному пути");
    }

    public List<Avatar> getAllAvatar(final Integer pageNumber, final Integer pageSize) {
        log.info("Was invoked method for : {}", getMethodName());
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        log.debug("Search all avatars");
        return avatarRepository.findAll(pageRequest).getContent();
    }

    private byte[] generateImagePreview(String filePath) {
        log.info("Was invoked method for : {}", getMethodName());
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

    private static String getMethodName() {
        return convertCamelCaseToSpace(Thread.currentThread().getStackTrace()[2].getMethodName());
    }

    private static String convertCamelCaseToSpace(String camelCaseString) {
        return camelCaseString.replaceAll("(?<=.)([A-Z])", " $1").toLowerCase();
    }
}
