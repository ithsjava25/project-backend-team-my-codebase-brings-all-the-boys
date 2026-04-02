package org.example.projectbackendteammycodebasebringsalltheboys.dto.schoolclass;

import java.util.List;
import java.util.UUID;
import lombok.Data;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.course.CourseDetailResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.user.UserResponse;

@Data
public class SchoolClassDetailResponse {
  private UUID id;
  private String name;
  private String description;
  private List<UserResponse> students;
  private List<UserResponse> teachers; // Mentors or lead teachers for the class
  private List<CourseDetailResponse> courses;
}
