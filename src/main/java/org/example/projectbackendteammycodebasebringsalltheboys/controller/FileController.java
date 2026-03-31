package org.example.projectbackendteammycodebasebringsalltheboys.controller;

import jakarta.validation.Valid;
import java.security.Principal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.file.FileResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.file.UploadRequest;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.file.UploadResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Assignment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Comment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.FileMetadata;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.mapper.DtoMapper;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.AssignmentRepository;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.CommentRepository;
import org.example.projectbackendteammycodebasebringsalltheboys.service.FileService;
import org.example.projectbackendteammycodebasebringsalltheboys.service.UserService;
import org.example.projectbackendteammycodebasebringsalltheboys.storage.StorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

  private final FileService fileService;
  private final UserService userService;
  private final StorageService storageService;
  private final AssignmentRepository assignmentRepository;
  private final CommentRepository commentRepository;
  private final DtoMapper dtoMapper;

  @PostMapping("/upload-url")
  public ResponseEntity<UploadResponse> getUploadUrl(
      @Valid @RequestBody UploadRequest request, Principal principal) {

    String s3Key =
        UUID.randomUUID().toString() + "_" + request.getFileName().replaceAll("\\s+", "_");
    String uploadUrl = storageService.generateUploadUrl(s3Key, request.getContentType());

    return ResponseEntity.ok(new UploadResponse(uploadUrl, s3Key));
  }

  @PostMapping("/finalize")
  public ResponseEntity<FileResponse> finalizeUpload(
      @Valid @RequestBody UploadRequest request, @RequestParam String s3Key, Principal principal) {

    User currentUser =
        userService
            .getUserByUsername(principal.getName())
            .orElseThrow(() -> new IllegalStateException("Current user not found"));

    Assignment assignment = null;
    if (request.getAssignmentId() != null) {
      assignment =
          assignmentRepository
              .findById(request.getAssignmentId())
              .orElseThrow(() -> new IllegalArgumentException("Assignment not found"));
    }

    Comment comment = null;
    if (request.getCommentId() != null) {
      comment =
          commentRepository
              .findById(request.getCommentId())
              .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
    }

    FileMetadata metadata =
        fileService.savePresignedMetadata(
            s3Key,
            request.getFileName(),
            request.getFileSize() != null ? request.getFileSize() : 0L,
            request.getContentType(),
            currentUser,
            assignment,
            comment);

    return ResponseEntity.ok(dtoMapper.toFileResponse(metadata));
  }

  @GetMapping("/{id}")
  public ResponseEntity<FileResponse> getFileMetadata(@PathVariable Long id, Principal principal) {
    // Basic access control: TEACHER/ADMIN or the Uploader can see metadata
    return fileService
        .getFileById(id)
        .map(dtoMapper::toFileResponse)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }
}
