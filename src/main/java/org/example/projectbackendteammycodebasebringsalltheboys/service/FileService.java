package org.example.projectbackendteammycodebasebringsalltheboys.service;

import java.io.InputStream;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Assignment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Comment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.FileMetadata;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.FileMetadataRepository;
import org.example.projectbackendteammycodebasebringsalltheboys.storage.StorageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FileService {

  private final FileMetadataRepository fileMetadataRepository;
  private final StorageService storageService;
  private final ActivityLogService activityLogService;

  @Transactional
  public FileMetadata uploadAssignmentFile(
      Assignment assignment,
      User uploader,
      String fileName,
      InputStream inputStream,
      long size,
      String contentType) {

    String s3Key = storageService.uploadFile(fileName, inputStream, size, contentType);

    try {
      FileMetadata metadata = new FileMetadata();
      metadata.setS3Key(s3Key);
      metadata.setFileName(fileName);
      metadata.setFileSize(size);
      metadata.setContentType(contentType);
      metadata.setAssignment(assignment);
      metadata.setUploader(uploader);

      FileMetadata saved = fileMetadataRepository.save(metadata);

      activityLogService.log(
          uploader,
          "UPLOADED_FILE",
          "Assignment",
          assignment.getId(),
          "Uploaded file: " + fileName);

      return saved;
    } catch (Exception e) {
      storageService.deleteFile(s3Key);
      throw e;
    }
  }

  @Transactional
  public FileMetadata uploadCommentFile(
      Comment comment,
      User uploader,
      String fileName,
      InputStream inputStream,
      long size,
      String contentType) {

    String s3Key = storageService.uploadFile(fileName, inputStream, size, contentType);

    try {
      FileMetadata metadata = new FileMetadata();
      metadata.setS3Key(s3Key);
      metadata.setFileName(fileName);
      metadata.setFileSize(size);
      metadata.setContentType(contentType);
      metadata.setComment(comment);
      metadata.setAssignment(comment.getAssignment());
      metadata.setUploader(uploader);

      FileMetadata saved = fileMetadataRepository.save(metadata);

      activityLogService.log(
          uploader,
          "UPLOADED_FILE",
          "Comment",
          comment.getId(),
          "Uploaded file: " + fileName + " for comment");

      return saved;
    } catch (Exception e) {
      storageService.deleteFile(s3Key);
      throw e;
    }
  }

  @Transactional(readOnly = true)
  public List<FileMetadata> getFilesByAssignment(Assignment assignment) {
    return fileMetadataRepository.findByAssignment(assignment);
  }

  @Transactional(readOnly = true)
  public java.util.Optional<FileMetadata> getFileById(Long id) {
    return fileMetadataRepository.findById(id);
  }

  @Transactional
  public FileMetadata savePresignedMetadata(
      String s3Key,
      String fileName,
      long size,
      String contentType,
      User uploader,
      Assignment assignment,
      Comment comment) {

    FileMetadata metadata = new FileMetadata();
    metadata.setS3Key(s3Key);
    metadata.setFileName(fileName);
    metadata.setFileSize(size);
    metadata.setContentType(contentType);
    metadata.setUploader(uploader);
    metadata.setAssignment(assignment);
    metadata.setComment(comment);

    FileMetadata saved = fileMetadataRepository.save(metadata);

    String targetType = assignment != null ? "Assignment" : "Comment";
    Long targetId = assignment != null ? assignment.getId() : comment.getId();

    activityLogService.log(
        uploader, "UPLOADED_FILE", targetType, targetId, "Uploaded file: " + fileName);

    return saved;
  }

  public String generateUploadUrl(String fileName, String contentType) {
    String s3Key = java.util.UUID.randomUUID().toString() + "_" + fileName.replaceAll("\\s+", "_");
    return storageService.generateUploadUrl(s3Key, contentType);
  }

  public String getS3KeyFromUrl(String uploadUrl) {
    // This is a bit hacky, but for S3 URLs the key is after the bucket name
    // In a real S3 URL it looks like https://bucket.s3.region.amazonaws.com/key
    // For local it's /api/files/upload-placeholder/key
    String[] parts = uploadUrl.split("/");
    return parts[parts.length - 1].split("\\?")[0];
  }

  public InputStream downloadFile(FileMetadata metadata) {
    return storageService.downloadFile(metadata.getS3Key());
  }
}
