package org.example.projectbackendteammycodebasebringsalltheboys.dto.comment;

import java.time.LocalDateTime;
import lombok.Data;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.user.UserResponse;

@Data
public class CommentResponse {
  private Long id;
  private String text;
  private UserResponse author;
  private LocalDateTime createdAt;
}
