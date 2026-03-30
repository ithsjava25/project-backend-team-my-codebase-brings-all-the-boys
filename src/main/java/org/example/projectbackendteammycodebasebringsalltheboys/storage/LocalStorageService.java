package org.example.projectbackendteammycodebasebringsalltheboys.storage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class LocalStorageService implements StorageService {

  private final Path root = Paths.get("uploads").toAbsolutePath().normalize();

  public LocalStorageService() {
    try {
      Files.createDirectories(root);
    } catch (IOException e) {
      throw new RuntimeException("Could not initialize storage folder", e);
    }
  }

  @Override
  public String uploadFile(
      String fileName, InputStream inputStream, long size, String contentType) {
    try {
      String sanitizedFileName = Paths.get(fileName).getFileName().toString();
      String s3Key = UUID.randomUUID().toString() + "_" + sanitizedFileName;
      Path targetPath = this.root.resolve(s3Key).toAbsolutePath().normalize();
      if (!targetPath.startsWith(this.root)) {
        throw new IllegalArgumentException("Invalid file name");
      }
      Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
      return s3Key;
    } catch (IOException e) {
      throw new RuntimeException("Could not store the file. Error: " + e.getMessage(), e);
    }
  }

  @Override
  public InputStream downloadFile(String s3Key) {
    try {
      Path file = this.root.resolve(s3Key).toAbsolutePath().normalize();
      if (!file.startsWith(this.root)) {
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
      Path file = this.root.resolve(s3Key).toAbsolutePath().normalize();
      if (!file.startsWith(this.root)) {
        throw new IllegalArgumentException("Invalid file key");
      }
      Files.deleteIfExists(file);
    } catch (IOException e) {
      throw new RuntimeException("Could not delete file: " + e.getMessage(), e);
    }
  }
}
