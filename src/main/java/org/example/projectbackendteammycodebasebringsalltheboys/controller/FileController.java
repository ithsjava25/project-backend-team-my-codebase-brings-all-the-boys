package org.example.projectbackendteammycodebasebringsalltheboys.controller;

import jakarta.validation.Valid;

import java.security.Principal;

import lombok.RequiredArgsConstructor;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.file.FileResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.file.GeneratedUpload;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.file.UploadRequest;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.file.UploadResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Assignment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Comment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.FileMetadata;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.mapper.DtoMapper;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.AssignmentRepository;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.CommentRepository;
import org.example.projectbackendteammycodebasebringsalltheboys.service.CaseService;
import org.example.projectbackendteammycodebasebringsalltheboys.service.CommentService;
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
    private final CaseService caseService;
    private final CommentService commentService;
    private final DtoMapper dtoMapper;

    @PostMapping("/upload-url")
    public ResponseEntity<UploadResponse> getUploadUrl(
            @Valid @RequestBody UploadRequest request, Principal principal) {

        GeneratedUpload generatedUpload =
                fileService.generateUploadUrl(request.getFileName(), request.getContentType());

        return ResponseEntity.ok(
                new UploadResponse(generatedUpload.uploadUrl(), generatedUpload.s3Key()));
    }

    @PostMapping("/finalize")
    public ResponseEntity<FileResponse> finalizeUpload(
            @Valid @RequestBody UploadRequest request, @RequestParam String s3Key, Principal principal) {

        if (principal == null) {
            return ResponseEntity.status(401).build();
        }

        if (s3Key == null || s3Key.isBlank()) {
            throw new IllegalArgumentException("s3Key is required");
        }

        if (request.getAssignmentId() == null && request.getCommentId() == null) {
            throw new IllegalArgumentException("Either assignmentId or commentId must be provided");
        }

        User currentUser =
                userService
                        .getUserByUsername(principal.getName())
                        .orElseThrow(() -> new IllegalStateException("Current user not found"));

        Assignment assignment = null;
        if (request.getAssignmentId() != null) {
            assignment =
                    caseService
                            .getCaseById(request.getAssignmentId())
                            .orElseThrow(() -> new IllegalArgumentException("Assignment not found"));
        }

        Comment comment = null;
        if (request.getCommentId() != null) {
            comment =
                    commentService
                            .getCommentById(request.getCommentId())
                            .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

            if (comment.getAssignment() == null) {
                throw new IllegalArgumentException("Comment is not linked to an assignment");
            }

            if (assignment != null && !comment.getAssignment().getId().equals(assignment.getId())) {
                throw new IllegalArgumentException("Comment does not belong to the specified assignment");
            }

            assignment = comment.getAssignment();
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
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }

        User currentUser = userService.getUserByUsername(principal.getName())
                .orElseThrow(() -> new IllegalStateException("Current user not found"));

        return fileService
                .getFileById(id)
                .filter(file -> canAccessFile(currentUser, file))
                .map(file -> ResponseEntity.ok(dtoMapper.toFileResponse(file)))
                .orElse(ResponseEntity.notFound().build());
    }

    private boolean canAccessFile(User user, FileMetadata file) {
        // Implement: TEACHER/ADMIN role check OR user is the uploader
        return user.equals(file.getUploader())
                || user.getRole().getName().equals("ADMIN")
                || user.getRole().getName().equals("TEACHER");
    }
}
