package org.example.projectbackendteammycodebasebringsalltheboys.dto.file;

import java.util.UUID;
import lombok.Data;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.user.UserResponse;

@Data
public class FileResponse {
  private UUID id;
  private String fileName;
  private Long fileSize;
  private String contentType;
  private UserResponse uploader;
  private String downloadUrl;
}
