package org.example.projectbackendteammycodebasebringsalltheboys.dto.course;

import java.util.UUID;
import lombok.Data;

@Data
public class CourseSurfaceResponse {
  private UUID id;
  private String name;
  private String schoolClassName;
}
