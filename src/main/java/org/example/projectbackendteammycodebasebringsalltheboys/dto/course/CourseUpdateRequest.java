package org.example.projectbackendteammycodebasebringsalltheboys.dto.course;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;

@Data
public class CourseUpdateRequest {
  @NotBlank private String name;
  private String description;
  @NotNull private UUID schoolClassId;
  private UUID leadTeacherId;
  private LocalDateTime endDate;
  // Assistants and enrollments would typically be managed via separate endpoints or
  // by passing lists of IDs, not directly in a single update DTO.
}
