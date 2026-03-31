package org.example.projectbackendteammycodebasebringsalltheboys.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class S3Config {

  @Value("${aws.s3.region:eu-north-1}")
  private String region;

  // These are dummy values, IMPORTANT to replace them with your own once AWS is set up (in
  // application.properties)
  @Value("${aws.s3.access-key:dummy}")
  private String accessKey;

  // These are dummy values, IMPORTANT to replace them with your own once AWS is set up (in
  // application.properties)
  @Value("${aws.s3.secret-key:dummy}")
  private String secretKey;

  @Bean
  public S3Client s3Client() {
    return S3Client.builder()
        .region(Region.of(region))
        .credentialsProvider(
            StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
        .build();
  }

  @Bean
  public S3Presigner s3Presigner() {
    return S3Presigner.builder()
        .region(Region.of(region))
        .credentialsProvider(
            StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
        .build();
  }
}
