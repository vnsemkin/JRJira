package com.javarush.jira.bugtracking.attachment;

import com.javarush.jira.common.error.IllegalRequestDataException;
import com.javarush.jira.common.error.NotFoundException;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@UtilityClass
public class FileUtil {
    private static final String ATTACHMENT_PATH = "./attachments/%s/";

    public static void upload(@NotNull MultipartFile multipartFile, String directoryPath, String fileName) {
        if (multipartFile.isEmpty()) {
            throw new IllegalRequestDataException("Select a file to upload.");
        }

        if (!StringUtils.hasText(directoryPath)) {
            throw new IllegalRequestDataException("Directory path is not provided.");
        }

        Path targetDirectory = Paths.get(directoryPath);
        if (!Files.exists(targetDirectory)) {
            try {
                Files.createDirectories(targetDirectory);
            } catch (IOException ex) {
                throw new IllegalRequestDataException("Failed to create directory: " + directoryPath);
            }
        }

        Path targetFile = targetDirectory.resolve(fileName);
        try {
            Files.write(targetFile, multipartFile.getBytes());
        } catch (IOException ex) {
            throw new IllegalRequestDataException("Failed to upload file: " + multipartFile.getOriginalFilename());
        }
    }


    public static @NotNull Resource download(String fileLink) {
        Path path = Paths.get(fileLink);
        try {
            Resource resource = new UrlResource(path.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new IllegalRequestDataException("Failed to download file " + resource.getFilename());
            }
        } catch (MalformedURLException ex) {
            throw new NotFoundException("File" + fileLink + " not found");
        }
    }

    public static void delete(String fileLink) {
        Path path = Paths.get(fileLink);
        try {
            Files.delete(path);
        } catch (IOException ex) {
            throw new IllegalRequestDataException("File" + fileLink + " deletion failed.");
        }
    }

    public static String getPath(@NotNull String titleType) {
        return String.format(ATTACHMENT_PATH, titleType.toLowerCase());
    }
}
