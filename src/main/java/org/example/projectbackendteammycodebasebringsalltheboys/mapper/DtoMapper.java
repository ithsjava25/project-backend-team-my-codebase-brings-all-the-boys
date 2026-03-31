package org.example.projectbackendteammycodebasebringsalltheboys.mapper;

import lombok.RequiredArgsConstructor;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.casefile.CaseResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.comment.CommentResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.file.FileResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.user.RoleResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.user.UserResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Assignment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Comment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.FileMetadata;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Role;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.storage.StorageService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DtoMapper {

  private final StorageService storageService;

  public CaseResponse toCaseResponse(Assignment assignment) {
    if (assignment == null) return null;
    CaseResponse response = new CaseResponse();
    response.setId(assignment.getId());
    response.setTitle(assignment.getTitle());
    response.setDescription(assignment.getDescription());
    response.setCreator(toUserResponse(assignment.getCreator()));
    response.setStatus(assignment.getStatus());
    response.setCreatedAt(assignment.getCreatedAt());
    response.setUpdatedAt(assignment.getUpdatedAt());
    return response;
  }

  public CommentResponse toCommentResponse(Comment comment) {
    if (comment == null) return null;
    CommentResponse response = new CommentResponse();
    response.setId(comment.getId());
    response.setText(comment.getText());
    response.setAuthor(toUserResponse(comment.getAuthor()));
    response.setCreatedAt(comment.getCreatedAt());
    return response;
  }

  public UserResponse toUserResponse(User user) {
    if (user == null) return null;
    UserResponse response = new UserResponse();
    response.setId(user.getId());
    response.setUsername(user.getUsername());
    response.setEmail(user.getEmail());
    response.setRole(toRoleResponse(user.getRole()));
    return response;
  }

  public RoleResponse toRoleResponse(Role role) {
    if (role == null) return null;
    RoleResponse response = new RoleResponse();
    response.setId(role.getId());
    response.setName(role.getName());
    return response;
  }

  public FileResponse toFileResponse(FileMetadata metadata) {
    if (metadata == null) return null;
    FileResponse response = new FileResponse();
    response.setId(metadata.getId());
    response.setFileName(metadata.getFileName());
    response.setFileSize(metadata.getFileSize());
    response.setContentType(metadata.getContentType());
    response.setUploader(toUserResponse(metadata.getUploader()));
    response.setDownloadUrl(storageService.generateDownloadUrl(metadata.getS3Key()));
    return response;
  }
}
