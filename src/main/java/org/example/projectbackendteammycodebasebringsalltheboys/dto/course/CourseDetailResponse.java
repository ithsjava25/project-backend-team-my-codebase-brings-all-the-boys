package org.example.projectbackendteammycodebasebringsalltheboys.dto.course;

import java.util.List;
import java.util.UUID;

import lombok.Data;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.assignment.AssignmentResponse; // Assuming AssignmentResponse exists
import org.example.projectbackendteammycodebasebringsalltheboys.dto.user.UserResponse;

@Data
public class CourseDetailResponse {
    private UUID id;
    private String name;
    private String description;
    private String schoolClassName;
    private UserResponse leadTeacher;
    private List<UserResponse> assistants;
    private List<AssignmentResponse> assignments;
}
