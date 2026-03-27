package org.example.projectbackendteammycodebasebringsalltheboys.dto.comment;

import lombok.Data;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.user.UserResponse;

import java.time.LocalDateTime;

@Data
public class CommentResponse {
    private Long id;
    private String text;
    private UserResponse author;
    private LocalDateTime createdAt;
}
