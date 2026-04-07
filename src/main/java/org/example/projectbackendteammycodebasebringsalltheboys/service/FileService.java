package org.example.projectbackendteammycodebasebringsalltheboys.service;

import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.projectbackendteammycodebasebringsalltheboys.annotation.LogActivity;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.file.GeneratedUpload;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Assignment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Comment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.FileMetadata;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.ActivityAction;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.EntityType;
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

  @LogActivity(action = ActivityAction.ADDED, entity = EntityType.FILE)
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

      return fileMetadataRepository.save(metadata);
    } catch (Exception e) {
      storageService.deleteFile(s3Key);
      throw e;
    }
  }

  @LogActivity(action = ActivityAction.ADDED, entity = EntityType.COMMENT_FILE)
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

      return fileMetadataRepository.save(metadata);
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
  public java.util.Optional<FileMetadata> getFileById(UUID id) {
    return fileMetadataRepository.findById(id);
  }

  @LogActivity(action = ActivityAction.ADDED, user = uploader, )
  @Transactional
  public FileMetadata savePresignedMetadata(
      String s3Key,
      String fileName,
      long size,
      String contentType,
      User uploader,
      Assignment assignment,
      Comment comment) {

    if ((assignment == null) == (comment == null)) {
      throw new IllegalArgumentException("Exactly one of assignment or comment must be provided");
    }

    FileMetadata metadata = new FileMetadata();
    metadata.setS3Key(s3Key);
    metadata.setFileName(fileName);
    metadata.setFileSize(size);
    metadata.setContentType(contentType);
    metadata.setUploader(uploader);
    metadata.setAssignment(assignment);
    metadata.setComment(comment);

    try {
      FileMetadata saved = fileMetadataRepository.save(metadata);

      String targetType = assignment != null ? "Assignment" : "Comment";
      UUID targetId = assignment != null ? assignment.getId() : comment.getId();

      activityLogService.log(
          uploader, "UPLOADED_FILE", targetType, targetId, "Uploaded file: " + fileName);

      return saved;
    } catch (Exception e) {
      storageService.deleteFile(s3Key);
      throw e;
    }
  }

  public GeneratedUpload generateUploadUrl(String fileName, String contentType) {
    String s3Key = java.util.UUID.randomUUID() + "_" + fileName.replaceAll("\\s+", "_");
    String uploadUrl = storageService.generateUploadUrl(s3Key, contentType);
    return new GeneratedUpload(uploadUrl, s3Key);
  }

  public String getS3KeyFromUrl(String uploadUrl) {
    if (uploadUrl == null || uploadUrl.isBlank()) {
      throw new IllegalArgumentException("uploadUrl must not be blank");
    }

    String path = URI.create(uploadUrl).getPath();
    if (path == null || path.isBlank()) {
      throw new IllegalArgumentException("Could not extract path from uploadUrl");
    }

    String localPrefix = "/api/files/upload-placeholder/";
    if (path.startsWith(localPrefix)) {
      path = path.substring(localPrefix.length());
    }

    while (path.startsWith("/")) {
      path = path.substring(1);
    }

    if (path.isBlank()) {
      throw new IllegalArgumentException("Could not extract S3 key from uploadUrl");
    }

    return path;
  }

  public InputStream downloadFile(FileMetadata metadata) {
    return storageService.downloadFile(metadata.getS3Key());
  }
}
