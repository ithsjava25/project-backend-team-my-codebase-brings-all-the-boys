package org.example.projectbackendteammycodebasebringsalltheboys.dto.user;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ActivityLogResponse {
    private Long id;
    private UserResponse user;
    private String action;
    private String entityType;
    private Long entityId;
    private String details;
    private LocalDateTime timestamp;
}
