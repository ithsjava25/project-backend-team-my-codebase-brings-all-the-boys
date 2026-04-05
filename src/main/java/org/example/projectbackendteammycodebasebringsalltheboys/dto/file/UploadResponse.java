package org.example.projectbackendteammycodebasebringsalltheboys.dto.file;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UploadResponse {
  private String uploadUrl;
  private String s3Key;
}
