package org.example.projectbackendteammycodebasebringsalltheboys.dto.assignment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
  @NotBlank(message = "Submission content cannot be blank")
  @Size(max = 10000, message = "Submission content is too long")
  private String content;

  @NotNull private List<String> fileS3Keys;
}
