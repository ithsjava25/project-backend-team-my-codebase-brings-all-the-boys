package org.example.projectbackendteammycodebasebringsalltheboys.controller;

import jakarta.validation.Valid;
import java.security.Principal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.file.FileResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.file.GeneratedUpload;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.file.UploadRequest;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.file.UploadResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Assignment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Comment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.FileMetadata;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.UserAssignment;
import org.example.projectbackendteammycodebasebringsalltheboys.exception.BadRequestException;
import org.example.projectbackendteammycodebasebringsalltheboys.exception.ForbiddenException;
import org.example.projectbackendteammycodebasebringsalltheboys.exception.NotFoundException;
import org.example.projectbackendteammycodebasebringsalltheboys.exception.UnauthorizedException;
import org.example.projectbackendteammycodebasebringsalltheboys.mapper.DtoMapper;
import org.example.projectbackendteammycodebasebringsalltheboys.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

  private final FileService fileService;
  private final UserService userService;
  private final CaseService caseService;
  private final CommentService commentService;
  private final UserAssignmentService userAssignmentService;
  private final AuthorizationService authorizationService;
  private final DtoMapper dtoMapper;

  @PostMapping("/upload-url")
  public ResponseEntity<UploadResponse> getUploadUrl(
      @Valid @RequestBody UploadRequest request, Principal principal) {

    // Prevent file upload to assignment if already submitted
    if (request.getUserAssignmentId() != null) {
      if (principal == null) {
        throw new UnauthorizedException("Authentication is required");
      }

      User currentUser =
          userService
              .getUserByUsername(principal.getName())
              .orElseThrow(() -> new UnauthorizedException("Current user not found"));

      UserAssignment userAssignment =
          userAssignmentService
              .getById(request.getUserAssignmentId())
              .orElseThrow(() -> new NotFoundException("UserAssignment not found"));

      if (!authorizationService.canAccessUserAssignment(currentUser, userAssignment)) {
        throw new ForbiddenException("You are not allowed to upload files to this assignment");
      }

      // Only allow upload for ASSIGNED status
      if (userAssignment.getStatus()
          != org.example.projectbackendteammycodebasebringsalltheboys.enums.StudentAssignmentStatus
              .ASSIGNED) {
        throw new ForbiddenException(
            "Cannot upload files. Assignment has already been submitted or evaluated.");
      }
    }

    GeneratedUpload generatedUpload =
        fileService.generateUploadUrl(request.getFileName(), request.getContentType());

    return ResponseEntity.ok(
        new UploadResponse(generatedUpload.uploadUrl(), generatedUpload.s3Key()));
  }

  @PostMapping("/finalize")
  public ResponseEntity<FileResponse> finalizeUpload(
      @Valid @RequestBody UploadRequest request, @RequestParam String s3Key, Principal principal) {

    if (principal == null) {
      throw new UnauthorizedException("Authentication is required");
    }

    if (s3Key == null || s3Key.isBlank()) {
      throw new BadRequestException("s3Key is required");
    }

    boolean hasAssignment = request.getAssignmentId() != null;
    boolean hasUserAssignment = request.getUserAssignmentId() != null;
    boolean hasComment = request.getCommentId() != null;

    int parentCount = (hasAssignment ? 1 : 0) + (hasUserAssignment ? 1 : 0) + (hasComment ? 1 : 0);
    if (parentCount != 1) {
      throw new BadRequestException(
          "Exactly one of assignmentId, userAssignmentId, or commentId must be provided");
    }

    User currentUser =
        userService
            .getUserByUsername(principal.getName())
            .orElseThrow(() -> new UnauthorizedException("Current user not found"));

    Assignment assignment = null;
    if (request.getAssignmentId() != null) {
      assignment =
          caseService
              .getCaseById(request.getAssignmentId())
              .orElseThrow(() -> new NotFoundException("Assignment not found"));

      if (!authorizationService.canAccessCase(currentUser, assignment)) {
        throw new ForbiddenException("You are not allowed to attach files to this assignment");
      }
    }

    UserAssignment userAssignment = null;
    if (request.getUserAssignmentId() != null) {
      userAssignment =
          userAssignmentService
              .getById(request.getUserAssignmentId())
              .orElseThrow(() -> new NotFoundException("UserAssignment not found"));

      if (!authorizationService.canAccessUserAssignment(currentUser, userAssignment)) {
        throw new ForbiddenException("You are not allowed to attach files to this user assignment");
      }

      if (userAssignment.getStatus()
          != org.example.projectbackendteammycodebasebringsalltheboys.enums.StudentAssignmentStatus
              .ASSIGNED) {
        throw new ForbiddenException(
            "Cannot finalize file upload. Assignment has already been submitted or evaluated.");
      }
    }

    Comment comment = null;
    if (request.getCommentId() != null) {
      comment =
          commentService
              .getCommentById(request.getCommentId())
              .orElseThrow(() -> new NotFoundException("Comment not found"));

      if (comment.getAssignment() == null) {
        throw new BadRequestException("Comment is not linked to an assignment");
      }

      if (assignment != null && !comment.getAssignment().getId().equals(assignment.getId())) {
        throw new BadRequestException("Comment does not belong to the specified assignment");
      }

      if (!authorizationService.canModifyComment(currentUser, comment)) {
        throw new ForbiddenException("You are not authorized to modify this comment");
      }

      // assignment = comment.getAssignment(); <-- The villain, apparently....
    }

    FileMetadata metadata =
        fileService.savePresignedMetadata(
            s3Key,
            request.getFileName(),
            request.getFileSize() != null ? request.getFileSize() : 0L,
            request.getContentType(),
            currentUser,
            assignment,
            userAssignment,
            comment);

    return ResponseEntity.ok(dtoMapper.toFileResponse(metadata));
  }

  @GetMapping("/{id}")
  public ResponseEntity<FileResponse> getFileMetadata(@PathVariable UUID id, Principal principal) {
    if (principal == null) {
      return ResponseEntity.status(401).build();
    }

    User currentUser =
        userService
            .getUserByUsername(principal.getName())
            .orElseThrow(() -> new UnauthorizedException("Current user not found"));

    return fileService
        .getFileById(id)
        .filter(file -> canAccessFile(currentUser, file))
        .map(file -> ResponseEntity.ok(dtoMapper.toFileResponse(file)))
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/{id}/download")
  public ResponseEntity<org.springframework.core.io.Resource> downloadFile(
      @PathVariable UUID id, Principal principal) {
    if (principal == null) {
      throw new org.example.projectbackendteammycodebasebringsalltheboys.exception
          .UnauthorizedException("Authentication is required");
    }

    User currentUser =
        userService
            .getUserByUsername(principal.getName())
            .orElseThrow(
                () ->
                    new org.example.projectbackendteammycodebasebringsalltheboys.exception
                        .UnauthorizedException("Current user not found"));

    FileMetadata fileMetadata =
        fileService
            .getFileById(id)
            .orElseThrow(
                () ->
                    new org.example.projectbackendteammycodebasebringsalltheboys.exception
                        .NotFoundException("File not found"));

    if (!canAccessFile(currentUser, fileMetadata)) {
      throw new org.example.projectbackendteammycodebasebringsalltheboys.exception
          .ForbiddenException("You are not authorized to download this file");
    }

    java.io.InputStream inputStream = fileService.downloadFile(fileMetadata);
    org.springframework.core.io.InputStreamResource resource =
        new org.springframework.core.io.InputStreamResource(inputStream);

    String contentType = fileMetadata.getContentType();
    if (contentType == null || contentType.isBlank()) {
      contentType = "application/octet-stream";
    }

    return ResponseEntity.ok()
        .contentType(org.springframework.http.MediaType.parseMediaType(contentType))
        .header(
            org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"" + fileMetadata.getFileName() + "\"")
        .body(resource);
  }

  private boolean canAccessFile(User user, FileMetadata file) {
    // 1. Uploader can always access their own files
    if (user.getId().equals(file.getUploader().getId())) {
      return true;
    }

    // 2. Admin and teachers can access all files
    if (isAdmin(user) || isTeacher(user)) {
      return true;
    }

    // 3. Students can only access files uploaded by teachers/admins (not other students)
    if (isStudent(user)) {
      boolean isFromTeacherOrAdmin =
          "ROLE_TEACHER".equals(file.getUploader().getRole().getName())
              || "ROLE_ADMIN".equals(file.getUploader().getRole().getName());

      if (isFromTeacherOrAdmin) {
        // If file is attached to an assignment
        if (file.getAssignment() != null
            && authorizationService.canAccessAssignmentDetails(user, file.getAssignment())) {
          return true;
        }

        // If file is attached to a comment
        if (file.getComment() != null
            && file.getComment().getAssignment() != null
            && authorizationService.canAccessAssignmentDetails(
                user, file.getComment().getAssignment())) {
          return true;
        }
      }
    }

    return false;
  }

  private boolean isAdmin(User user) {
    return user.getRole() != null && "ROLE_ADMIN".equals(user.getRole().getName());
  }

  private boolean isTeacher(User user) {
    return user.getRole() != null && "ROLE_TEACHER".equals(user.getRole().getName());
  }

  private boolean isStudent(User user) {
    return user.getRole() != null && "ROLE_STUDENT".equals(user.getRole().getName());
  }
}
