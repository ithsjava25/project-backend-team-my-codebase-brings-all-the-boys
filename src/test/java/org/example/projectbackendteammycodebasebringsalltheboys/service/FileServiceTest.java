package org.example.projectbackendteammycodebasebringsalltheboys.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Assignment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Comment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.FileMetadata;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.FileMetadataRepository;
import org.example.projectbackendteammycodebasebringsalltheboys.storage.StorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

  @Mock private FileMetadataRepository fileMetadataRepository;
  @Mock private StorageService storageService;
  @Mock private ActivityLogService activityLogService;

  private FileService fileService;

  @BeforeEach
  void setUp() {
    fileService = new FileService(fileMetadataRepository, storageService, activityLogService);
  }

  @Test
  @DisplayName("uploadAssignmentFile saves metadata after successful upload")
  void uploadAssignmentFile_success_savesMetadata() {
    Assignment assignment = new Assignment();
    User uploader = new User();
    String fileName = "test.txt";
    InputStream is = new ByteArrayInputStream("hello".getBytes());
    when(storageService.uploadFile(anyString(), any(), anyLong(), anyString())).thenReturn("s3key");
    when(fileMetadataRepository.save(any(FileMetadata.class))).thenAnswer(inv -> inv.getArgument(0));

    FileMetadata result = fileService.uploadAssignmentFile(assignment, uploader, fileName, is, 5L, "text/plain");

    assertThat(result.getS3Key()).isEqualTo("s3key");
    assertThat(result.getFileName()).isEqualTo(fileName);
    assertThat(result.getAssignment()).isEqualTo(assignment);
    verify(fileMetadataRepository).save(any(FileMetadata.class));
  }

  @Test
  @DisplayName("uploadAssignmentFile deletes file from storage if repository save fails")
  void uploadAssignmentFile_saveFails_deletesFromStorage() {
    Assignment assignment = new Assignment();
    User uploader = new User();
    when(storageService.uploadFile(anyString(), any(), anyLong(), anyString())).thenReturn("s3key");
    when(fileMetadataRepository.save(any())).thenThrow(new RuntimeException("DB error"));

    assertThatThrownBy(() -> fileService.uploadAssignmentFile(assignment, uploader, "file", null, 0L, "type"))
        .isInstanceOf(RuntimeException.class);

    verify(storageService).deleteFile("s3key");
  }

  @Test
  @DisplayName("savePresignedMetadata throws exception if both assignment and comment are null or both present")
  void savePresignedMetadata_invalidInput_throwsException() {
    User uploader = new User();
    assertThatThrownBy(() -> fileService.savePresignedMetadata("key", "file", 0L, "type", uploader, null, null))
        .isInstanceOf(IllegalArgumentException.class);

    assertThatThrownBy(() -> fileService.savePresignedMetadata("key", "file", 0L, "type", uploader, new Assignment(), new Comment()))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("getS3KeyFromUrl extracts key from various URL formats")
  void getS3KeyFromUrl_extractsCorrectly() {
    String url = "http://localhost:8080/api/files/upload-placeholder/my-key-123";
    assertThat(fileService.getS3KeyFromUrl(url)).isEqualTo("my-key-123");

    String simpleUrl = "http://localhost:8080/some-key";
    assertThat(fileService.getS3KeyFromUrl(simpleUrl)).isEqualTo("some-key");
  }
}
