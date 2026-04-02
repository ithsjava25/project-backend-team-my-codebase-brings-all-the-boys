package org.example.projectbackendteammycodebasebringsalltheboys.dto.schoolclass;

import java.util.UUID;
import lombok.Data;

@Data
public class SchoolClassSurfaceResponse {
  private UUID id;
  private String name;
  private String description;
}
