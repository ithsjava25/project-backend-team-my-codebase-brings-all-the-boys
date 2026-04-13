package org.example.projectbackendteammycodebasebringsalltheboys.dto.assignment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.comment.CommentResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.file.FileResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.user.UserResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.AssignmentStatus;

@Data
public class AssignmentDetailResponse {
  private UUID id;
  private String title;
  private String description;
  private UserResponse creator;
  private AssignmentStatus status;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private LocalDateTime deadline;
  private List<CommentResponse> comments;
  private List<FileResponse> files; // Assuming FileResponse exists
}
