package org.example.projectbackendteammycodebasebringsalltheboys.storage;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class LocalStorageService implements StorageService {

    private final Path root = Paths.get("uploads");

    public LocalStorageService() {
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage folder", e);
        }
    }

    @Override
    public String uploadFile(String fileName, InputStream inputStream, long size, String contentType) {
        try {
            String s3Key = UUID.randomUUID().toString() + "_" + fileName;
            Files.copy(inputStream, this.root.resolve(s3Key), StandardCopyOption.REPLACE_EXISTING);
            return s3Key;
        } catch (Exception e) {
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
    }

    @Override
    public InputStream downloadFile(String s3Key) {
        try {
            Path file = root.resolve(s3Key).normalize();
            if (!file.startsWith(root)) {
                throw new IllegalArgumentException("Invalid file key");
            }
            return Files.newInputStream(file);
        } catch (IOException e) {
            throw new RuntimeException("Could not read file: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteFile(String s3Key) {
        try {
            Path file = root.resolve(s3Key).normalize();
            if (!file.startsWith(root)) {
                throw new IllegalArgumentException("Invalid file key");
            }
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new RuntimeException("Could not delete file: " + e.getMessage(), e);
        }
    }
}
