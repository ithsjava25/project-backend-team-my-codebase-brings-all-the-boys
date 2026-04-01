package org.example.projectbackendteammycodebasebringsalltheboys.config;

import org.example.projectbackendteammycodebasebringsalltheboys.storage.LocalStorageService;
import org.example.projectbackendteammycodebasebringsalltheboys.storage.S3StorageService;
import org.example.projectbackendteammycodebasebringsalltheboys.storage.StorageService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class StorageConfig {

  @Bean
  @ConditionalOnProperty(name = "storage.type", havingValue = "s3")
  public StorageService s3StorageService(
      S3Client s3Client, S3Presigner s3Presigner, String bucketName) {
    return new S3StorageService(s3Client, s3Presigner, bucketName);
  }

  @Bean
  @ConditionalOnProperty(name = "storage.type", havingValue = "local", matchIfMissing = true)
  public StorageService localStorageService() {
    return new LocalStorageService();
  }
}
