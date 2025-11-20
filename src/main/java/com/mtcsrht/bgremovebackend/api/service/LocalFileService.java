package com.mtcsrht.bgremovebackend.api.service;

import com.mtcsrht.bgremovebackend.api.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class LocalFileService implements FileService {

    private final Path rootLocation;

    public LocalFileService(@Value("${app.storage.location}") String storageLocation) {
        this.rootLocation = Paths.get(storageLocation).toAbsolutePath().normalize();
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage directory", e);
        }
    }

    @Override
    public String store(MultipartFile file, String folder) {
        try {
            String originalFilename = file.getOriginalFilename();
            String ext = getExtensionOrDefault(originalFilename, ".bin");
            String filename = UUID.randomUUID() + ext;

            Path folderPath = rootLocation.resolve(folder);
            Files.createDirectories(folderPath);

            Path target = folderPath.resolve(filename);
            Files.copy(file.getInputStream(), target);

            // objectKey is a portable "pointer" we can save in DB
            return folder + "/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    @Override
    public String store(byte[] bytes, String folder, String filenameHint) {
        try {
            String ext = getExtensionOrDefault(filenameHint, ".bin");
            String filename = UUID.randomUUID() + ext;

            Path folderPath = rootLocation.resolve(folder);
            Files.createDirectories(folderPath);

            Path target = folderPath.resolve(filename);
            Files.write(target, bytes);

            return folder + "/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    @Override
    public Resource loadAsResource(String objectKey) {
        try {
            Path file = rootLocation.resolve(objectKey).normalize();
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("File not found or not readable: " + objectKey);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("File not found: " + objectKey, e);
        }
    }

    @Override
    public void delete(String objectKey) {
        try {
            Path file = rootLocation.resolve(objectKey).normalize();
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file: " + objectKey, e);
        }
    }

    private String getExtensionOrDefault(String filename, String def) {
        if (filename == null) return def;
        int idx = filename.lastIndexOf('.');
        if (idx == -1) return def;
        return filename.substring(idx);
    }
}
