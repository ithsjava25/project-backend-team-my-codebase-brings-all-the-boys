package org.example.projectbackendteammycodebasebringsalltheboys.dto.assignment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.user.UserResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.StudentAssignmentStatus;

@Data
public class UserAssignmentResponse {
  private UUID id;
  private UUID assignmentId;
  private UserResponse student;
  private StudentAssignmentStatus status;
  private String feedback;
  private String grade;
  private LocalDateTime turnedInAt;
  private List<SubmissionResponse> submissions;
}
