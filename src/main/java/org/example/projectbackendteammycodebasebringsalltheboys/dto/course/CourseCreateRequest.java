package org.example.projectbackendteammycodebasebringsalltheboys.dto.course;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;

@Data
public class CourseCreateRequest {
  @NotBlank private String name;
  private String description;
  @NotNull private UUID schoolClassId; // Assuming ID is passed for association
  private UUID leadTeacherId; // Optional
  private LocalDateTime endDate;
}
