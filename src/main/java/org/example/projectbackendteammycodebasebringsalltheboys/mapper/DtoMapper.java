package org.example.projectbackendteammycodebasebringsalltheboys.mapper;

import java.util.Collections;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.assignment.AssignmentDetailResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.assignment.AssignmentResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.casefile.CaseResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.comment.CommentResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.course.CourseDetailResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.course.CourseSurfaceResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.file.FileResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.schoolclass.SchoolClassDetailResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.schoolclass.SchoolClassSurfaceResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.user.ActivityLogResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.user.RoleResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.user.UserResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.*;
import org.example.projectbackendteammycodebasebringsalltheboys.storage.StorageService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DtoMapper {

  private final StorageService storageService;

  public CaseResponse toCaseResponse(Assignment assignment) {
    if (assignment == null) return null;
    CaseResponse response = new CaseResponse();
    response.setId(assignment.getId());
    response.setTitle(assignment.getTitle());
    response.setDescription(assignment.getDescription());
    response.setCreator(toUserResponse(assignment.getCreator()));
    response.setStatus(assignment.getStatus());
    response.setCreatedAt(assignment.getCreatedAt());
    response.setUpdatedAt(assignment.getUpdatedAt());
    return response;
  }

  public CommentResponse toCommentResponse(Comment comment) {
    if (comment == null) return null;
    CommentResponse response = new CommentResponse();
    response.setId(comment.getId());
    response.setText(comment.getText());
    response.setAuthor(toUserResponse(comment.getAuthor()));
    response.setCreatedAt(comment.getCreatedAt());
    return response;
  }

  public UserResponse toUserResponse(User user) {
    if (user == null) return null;
    UserResponse response = new UserResponse();
    response.setId(user.getId());
    response.setUsername(user.getUsername());
    response.setEmail(user.getEmail());
    response.setRole(toRoleResponse(user.getRole()));
    return response;
  }

  public RoleResponse toRoleResponse(Role role) {
    if (role == null) return null;
    RoleResponse response = new RoleResponse();
    response.setId(role.getId());
    response.setName(role.getName());
    return response;
  }

  public FileResponse toFileResponse(FileMetadata metadata) {
    if (metadata == null) return null;
    FileResponse response = new FileResponse();
    response.setId(metadata.getId());
    response.setFileName(metadata.getFileName());
    response.setFileSize(metadata.getFileSize());
    response.setContentType(metadata.getContentType());
    response.setUploader(toUserResponse(metadata.getUploader()));
    if (metadata.getS3Key() != null) {
      response.setDownloadUrl(storageService.generateDownloadUrl(metadata.getS3Key()));
    }
    return response;
  }

  public ActivityLogResponse toActivityLogResponse(ActivityLog log) {
    if (log == null) return null;
    ActivityLogResponse response = new ActivityLogResponse();
    response.setId(log.getId());
    response.setAction(log.getAction());
    response.setEntityType(log.getEntityType());
    response.setEntityId(log.getEntityId());
    response.setDetails(log.getDetails());
    response.setTimestamp(log.getTimestamp());
    return response;
  }

  public CourseSurfaceResponse toCourseSurfaceResponse(Course course) {
    if (course == null) return null;
    CourseSurfaceResponse response = new CourseSurfaceResponse();
    response.setId(course.getId());
    response.setName(course.getName());
    if (course.getSchoolClass() != null) {
      response.setSchoolClassName(course.getSchoolClass().getName());
    }
    return response;
  }

  public CourseDetailResponse toCourseDetailResponse(Course course) {
    if (course == null) return null;
    CourseDetailResponse response = new CourseDetailResponse();
    response.setId(course.getId());
    response.setName(course.getName());
    response.setDescription(course.getDescription());
    if (course.getSchoolClass() != null) {
      response.setSchoolClassName(course.getSchoolClass().getName());
    }
    response.setLeadTeacher(toUserResponse(course.getLeadTeacher()));
    response.setAssistants(
        course.getAssistants() != null
            ? course.getAssistants().stream().map(this::toUserResponse).collect(Collectors.toList())
            : Collections.emptyList());
    response.setAssignments(
        course.getAssignments() != null
            ? course.getAssignments().stream()
                .map(this::toAssignmentResponse)
                .collect(Collectors.toList())
            : Collections.emptyList());
    return response;
  }

  public AssignmentResponse toAssignmentResponse(Assignment assignment) {
    if (assignment == null) return null;
    AssignmentResponse response = new AssignmentResponse();
    response.setId(assignment.getId());
    response.setTitle(assignment.getTitle());
    response.setStatus(assignment.getStatus());
    response.setCreatedAt(assignment.getCreatedAt());
    response.setUpdatedAt(assignment.getUpdatedAt());
    return response;
  }

  public AssignmentDetailResponse toAssignmentDetailResponse(Assignment assignment) {
    if (assignment == null) return null;
    AssignmentDetailResponse response = new AssignmentDetailResponse();
    response.setId(assignment.getId());
    response.setTitle(assignment.getTitle());
    response.setDescription(assignment.getDescription());
    response.setCreator(toUserResponse(assignment.getCreator()));
    response.setStatus(assignment.getStatus());
    response.setCreatedAt(assignment.getCreatedAt());
    response.setUpdatedAt(assignment.getUpdatedAt());
    // Files and comments would need lists, assuming they are available on the entity
    return response;
  }

  public SchoolClassSurfaceResponse toSchoolClassSurfaceResponse(SchoolClass sc) {
    if (sc == null) return null;
    SchoolClassSurfaceResponse response = new SchoolClassSurfaceResponse();
    response.setId(sc.getId());
    response.setName(sc.getName());
    response.setDescription(sc.getDescription());
    return response;
  }

  public SchoolClassDetailResponse toSchoolClassDetailResponse(SchoolClass sc) {
    if (sc == null) return null;
    SchoolClassDetailResponse response = new SchoolClassDetailResponse();
    response.setId(sc.getId());
    response.setName(sc.getName());
    response.setDescription(sc.getDescription());
    // Populate students and teachers based on enrollments
    response.setStudents(
        sc.getEnrollments().stream()
            .filter(
                e ->
                    e.getClassRole()
                        == org.example.projectbackendteammycodebasebringsalltheboys.enums.ClassRole
                            .STUDENT)
            .map(e -> toUserResponse(e.getUser()))
            .collect(Collectors.toList()));
    response.setTeachers(
        sc.getEnrollments().stream()
            .filter(
                e ->
                    e.getClassRole()
                            == org.example.projectbackendteammycodebasebringsalltheboys.enums
                                .ClassRole.TEACHER
                        || e.getClassRole()
                            == org.example.projectbackendteammycodebasebringsalltheboys.enums
                                .ClassRole.MENTOR)
            .map(e -> toUserResponse(e.getUser()))
            .collect(Collectors.toList()));
    response.setCourses(
        sc.getCourses().stream().map(this::toCourseDetailResponse).collect(Collectors.toList()));
    return response;
  }
}
