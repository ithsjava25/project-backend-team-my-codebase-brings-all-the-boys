package org.example.projectbackendteammycodebasebringsalltheboys.dto.assignment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class EvaluationRequest {
  @NotBlank(message = "Betyg krävs")
  @Pattern(regexp = "^[A-F]$", message = "Betyget måste vara A, B, C, D, E eller F")
  private String grade;

  private String feedback;
}
