package org.example.projectbackendteammycodebasebringsalltheboys.dto.assignment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.file.FileResponse;

@Data
public class SubmissionResponse {
  private UUID id;
  private String content;
  private LocalDateTime submittedAt;
  private List<FileResponse> files;
}
