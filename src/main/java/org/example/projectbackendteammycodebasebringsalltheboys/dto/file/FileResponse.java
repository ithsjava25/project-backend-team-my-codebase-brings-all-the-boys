package org.example.projectbackendteammycodebasebringsalltheboys.dto.file;

import lombok.Data;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.user.UserResponse;

import java.time.LocalDateTime;

@Data
public class FileResponse {
    private Long id;
    private String fileName;
    private Long fileSize;
    private String contentType;
    private String downloadUrl;
    private UserResponse uploader;
    private LocalDateTime createdAt;
}
