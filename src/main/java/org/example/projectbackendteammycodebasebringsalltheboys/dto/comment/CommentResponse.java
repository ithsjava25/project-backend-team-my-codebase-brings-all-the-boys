package org.example.projectbackendteammycodebasebringsalltheboys.dto.comment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.file.FileResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.user.UserResponse;

@Data
public class CommentResponse {
  private UUID id;
  private String text;
  private UserResponse author;
  private LocalDateTime createdAt;
  private List<FileResponse> files;
}
