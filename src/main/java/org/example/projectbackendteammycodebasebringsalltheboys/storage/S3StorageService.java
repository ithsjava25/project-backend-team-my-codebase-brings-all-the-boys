package org.example.projectbackendteammycodebasebringsalltheboys.storage;

import java.io.InputStream;
import java.time.Duration;
import java.util.UUID;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

public class S3StorageService implements StorageService {

  private final S3Client s3Client;
  private final S3Presigner s3Presigner;
  private final String bucketName;

  public S3StorageService(S3Client s3Client, S3Presigner s3Presigner, String bucketName) {
    this.s3Client = s3Client;
    this.s3Presigner = s3Presigner;
    this.bucketName = bucketName;
  }

  @Override
  public String uploadFile(
      String fileName, InputStream inputStream, long size, String contentType) {
    String s3Key = UUID.randomUUID() + "_" + fileName;

    PutObjectRequest putObjectRequest =
        PutObjectRequest.builder()
            .bucket(bucketName)
            .key(s3Key)
            .contentType(contentType)
            .contentLength(size)
            .build();

    s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, size));

    return s3Key;
  }

  /**
   * Downloads a file from S3 as an {@link InputStream}.
   *
   * <p>The returned stream is the underlying {@link ResponseInputStream} produced by {@link
   * S3Client#getObject(GetObjectRequest)}. Callers are responsible for closing the returned stream
   * to ensure network resources are released.
   *
   * @param s3Key the S3 object key
   * @return an open input stream for the requested S3 object
   *     <p>Example:
   *     <p>try (InputStream inputStream = storageService.downloadFile(s3Key)) { // read the file
   *     here }
   */
  @Override
  public InputStream downloadFile(String s3Key) {
    GetObjectRequest getObjectRequest =
        GetObjectRequest.builder().bucket(bucketName).key(s3Key).build();

    return s3Client.getObject(getObjectRequest);
  }

  @Override
  public void deleteFile(String s3Key) {
    DeleteObjectRequest deleteObjectRequest =
        DeleteObjectRequest.builder().bucket(bucketName).key(s3Key).build();

    s3Client.deleteObject(deleteObjectRequest);
  }

  @Override
  public String generateDownloadUrl(String s3Key) {
    GetObjectRequest getObjectRequest =
        GetObjectRequest.builder().bucket(bucketName).key(s3Key).build();

    GetObjectPresignRequest presignRequest =
        GetObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(15))
            .getObjectRequest(getObjectRequest)
            .build();

    return s3Presigner.presignGetObject(presignRequest).url().toString();
  }

  @Override
  public String generateUploadUrl(String s3Key, String contentType) {
    PutObjectRequest putObjectRequest =
        PutObjectRequest.builder().bucket(bucketName).key(s3Key).contentType(contentType).build();

    PutObjectPresignRequest presignRequest =
        PutObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(15))
            .putObjectRequest(putObjectRequest)
            .build();

    return s3Presigner.presignPutObject(presignRequest).url().toString();
  }
}
