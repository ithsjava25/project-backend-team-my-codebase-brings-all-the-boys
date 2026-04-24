package org.example.projectbackendteammycodebasebringsalltheboys.dto.assignment;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionRequest {
  private String content;
  private List<String> fileS3Keys;
}
