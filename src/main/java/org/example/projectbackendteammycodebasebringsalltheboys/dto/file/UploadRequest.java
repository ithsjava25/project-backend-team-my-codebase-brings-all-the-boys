package org.example.projectbackendteammycodebasebringsalltheboys.dto.file;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
import lombok.Data;

@Data
public class UploadRequest {
  @NotBlank private String fileName;
  @NotBlank private String contentType;
  private Long fileSize;
  private UUID assignmentId;
  private UUID commentId;
}
