package org.example.projectbackendteammycodebasebringsalltheboys.dto.comment;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentRequest {
    @NotBlank(message = "Comment text is required")
    private String text;
}
