package org.example.projectbackendteammycodebasebringsalltheboys.dto.casefile;

import lombok.Data;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.user.UserResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.AssignmentStatus;

import java.time.LocalDateTime;

@Data
public class CaseResponse {
    private Long id;
    private String title;
    private String description;
    private UserResponse creator;
    private AssignmentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
