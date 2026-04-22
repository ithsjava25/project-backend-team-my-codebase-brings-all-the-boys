package org.example.projectbackendteammycodebasebringsalltheboys.dto.course;

import java.util.UUID;
import lombok.Data;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.user.UserSummary;

@Data
public class CourseSurfaceResponse {
  private UUID id;
  private String name;
  private String description;
  private String schoolClassName;
  private UserSummary leadTeacher;

  private java.time.LocalDateTime endDate;
}
