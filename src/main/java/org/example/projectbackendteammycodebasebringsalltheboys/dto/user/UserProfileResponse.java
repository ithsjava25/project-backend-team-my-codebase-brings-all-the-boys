package org.example.projectbackendteammycodebasebringsalltheboys.dto.user;

import java.util.List;
import java.util.UUID;
import lombok.Data;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.course.CourseSurfaceResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.schoolclass.SchoolClassSurfaceResponse;

@Data
public class UserProfileResponse {
  private UUID id;
  private String username;
  private String email;
  private RoleResponse role;
  private List<SchoolClassSurfaceResponse> classes;
  private List<CourseSurfaceResponse> courses;
}
