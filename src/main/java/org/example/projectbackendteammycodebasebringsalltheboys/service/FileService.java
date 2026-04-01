package org.example.projectbackendteammycodebasebringsalltheboys.service;

import java.io.InputStream;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.projectbackendteammycodebasebringsalltheboys.annotation.LogActivity;
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

  @LogActivity(action = ActivityAction.ADDED, entity = EntityType.FILE, entityIdParamIndex = 0)
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

  @LogActivity(action = ActivityAction.ADDED, entity = EntityType.COMMENT_FILE, entityIdParamIndex = 0)
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

  public InputStream downloadFile(FileMetadata metadata) {
    return storageService.downloadFile(metadata.getS3Key());
  }
}
