package org.example.projectbackendteammycodebasebringsalltheboys.dto.file;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UploadRequest {
  @NotBlank private String fileName;
  @NotBlank private String contentType;
  private Long fileSize;
  private Long assignmentId;
  private Long commentId;
}
