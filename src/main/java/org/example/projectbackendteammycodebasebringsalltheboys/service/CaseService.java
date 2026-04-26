package org.example.projectbackendteammycodebasebringsalltheboys.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.example.projectbackendteammycodebasebringsalltheboys.annotation.LogActivity;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.assignment.AssignmentDetailResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.assignment.AssignmentResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.assignment.AssignmentUpdateRequest;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.casefile.CaseRequest;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Assignment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Course;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.UserAssignment;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.ActivityAction;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.ActivityStatus;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.AssignmentStatus;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.EntityType;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.StudentAssignmentStatus;
import org.example.projectbackendteammycodebasebringsalltheboys.exception.BadRequestException;
import org.example.projectbackendteammycodebasebringsalltheboys.exception.ForbiddenException;
import org.example.projectbackendteammycodebasebringsalltheboys.exception.NotFoundException;
import org.example.projectbackendteammycodebasebringsalltheboys.mapper.DtoMapper;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.AssignmentRepository;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.CommentRepository;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.CourseRepository;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.UserAssignmentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CaseService {

  private final AssignmentRepository assignmentRepository;
  private final UserAssignmentRepository userAssignmentRepository;
  private final DtoMapper dtoMapper;
  private final AuthorizationService authorizationService;
  private final CourseRepository courseRepository;
  private final ActivityLogService activityLogService;
  private final CommentRepository commentRepository;

  @Transactional
  public AssignmentDetailResponse createCase(CaseRequest request, User creator) {
    Assignment assignment = new Assignment();
    assignment.setTitle(request.getTitle());
    assignment.setDescription(request.getDescription());
    assignment.setCreator(creator);
    assignment.setDeadline(request.getDeadline());
    assignment.setStatus(AssignmentStatus.OPEN);

    Assignment saved = assignmentRepository.save(assignment);
    activityLogService.log(
        creator,
        saved.getId(),
        ActivityAction.CREATED,
        EntityType.ASSIGNMENT,
        null,
        Map.of("title", saved.getTitle()),
        ActivityStatus.SUCCESS);

    return dtoMapper.toAssignmentDetailResponse(saved);
  }

  @Transactional
  public AssignmentDetailResponse updateAssignment(
      UUID id, AssignmentUpdateRequest request, User updater) {
    Assignment assignment =
        assignmentRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Assignment not found"));

    if (!authorizationService.canModifyAssignment(updater, assignment)) {
      throw new ForbiddenException("You do not have permission to modify this assignment");
    }

    java.util.List<String> updatedFields = new java.util.ArrayList<>();

    if (request.getTitle() != null) {
      assignment.setTitle(request.getTitle());
      updatedFields.add("title");
    }
    if (request.getDescription() != null) {
      assignment.setDescription(request.getDescription());
      updatedFields.add("description");
    }
    if (request.getDeadline() != null) {
      assignment.setDeadline(request.getDeadline());
      updatedFields.add("deadline");
    }
    if (request.getStatus() != null) {
      assignment.setStatus(request.getStatus());
      updatedFields.add("status");
    }

    if (request.getCourseId() != null) {
      Course course =
          courseRepository
              .findById(request.getCourseId())
              .orElseThrow(() -> new NotFoundException("Course not found"));
      assignment.setCourse(course);
      updatedFields.add("course");
    }

    if (assignment.getCourse() != null
        && assignment.getDeadline() != null
        && assignment.getCourse().getEndDate() != null
        && assignment.getDeadline().isAfter(assignment.getCourse().getEndDate())) {
      throw new BadRequestException("Deadline cannot be after course end date");
    }

    Assignment saved = assignmentRepository.save(assignment);
    activityLogService.log(
        updater,
        saved.getId(),
        ActivityAction.UPDATED,
        EntityType.ASSIGNMENT,
        null,
        Map.of("title", saved.getTitle(), "updatedFields", String.join(",", updatedFields)),
        ActivityStatus.SUCCESS);

    return dtoMapper.toAssignmentDetailResponse(saved);
  }

  @Transactional(readOnly = true)
  public Page<AssignmentResponse> getAccessibleAssignmentsDto(User user, Pageable pageable) {
    if (user == null) return Page.empty();

    String roleName = user.getRole() != null ? user.getRole().getName() : "";

    Page<Assignment> assignments =
        switch (roleName) {
          case "ROLE_ADMIN" -> assignmentRepository.findAll(pageable);
          case "ROLE_TEACHER" ->
              assignmentRepository.findAccessibleByTeacher(user.getId(), pageable);
          case "ROLE_STUDENT" ->
              assignmentRepository.findByStudentEnrollment(user.getId(), pageable);
          default -> Page.empty();
        };

    if (roleName.equals("ROLE_STUDENT")) {
      // Optimization: Fetch UserAssignments for the student for the assignments on this page
      List<UserAssignment> userAssignments =
          userAssignmentRepository.findByStudentAndAssignmentIn(user, assignments.getContent());

      Map<UUID, StudentAssignmentStatus> statusMap = new HashMap<>();
      for (UserAssignment ua : userAssignments) {
        statusMap.put(ua.getAssignment().getId(), ua.getStatus());
      }

      return assignments.map(
          a -> {
            AssignmentResponse resp = dtoMapper.toAssignmentResponse(a);
            resp.setStudentStatus(statusMap.get(a.getId()));
            return resp;
          });
    }

    return assignments.map(dtoMapper::toAssignmentResponse);
  }

  @Transactional(readOnly = true)
  public List<AssignmentResponse> getAccessibleAssignments(User user) {
    // Keep for backward compatibility if needed, but preferably use the paged version
    return assignmentRepository.findAll().stream()
        .filter(a -> authorizationService.canViewAssignment(user, a))
        .map(dtoMapper::toAssignmentResponse)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public Optional<AssignmentDetailResponse> getAccessibleAssignmentDetail(UUID id, User user) {
    return assignmentRepository
        .findById(id)
        .filter(a -> authorizationService.canAccessAssignmentDetails(user, a))
        .map(dtoMapper::toAssignmentDetailResponse);
  }

  @Transactional
  public void deleteAssignment(UUID id, User updater) {
    Assignment assignment =
        assignmentRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Assignment not found"));

    if (!authorizationService.canModifyAssignment(updater, assignment)) {
      throw new ForbiddenException("You do not have permission to delete this assignment");
    }

    activityLogService.log(
        updater,
        assignment.getId(),
        ActivityAction.DELETED,
        EntityType.ASSIGNMENT,
        null,
        Map.of("title", assignment.getTitle()),
        ActivityStatus.SUCCESS);

    // Delete all comments linked to this assignment (both assignment-scoped and
    // user-assignment-scoped)
    commentRepository.deleteByAssignment(assignment);

    assignmentRepository.delete(assignment);
  }

  @Transactional
  @LogActivity(
      action = ActivityAction.CREATED,
      entityType = EntityType.ASSIGNMENT,
      orphan = true,
      actorParamIndex = 2)
  public Assignment createCase(String title, String description, User creator) {
    return createCase(title, description, creator, null, null);
  }

  @Transactional
  @LogActivity(
      action = ActivityAction.CREATED,
      entityType = EntityType.ASSIGNMENT,
      orphan = true,
      actorParamIndex = 2)
  public Assignment createCase(String title, String description, User creator, Course course) {
    return createCase(title, description, creator, course, null);
  }

  @LogActivity(action = ActivityAction.CREATED, entityType = EntityType.ASSIGNMENT, orphan = true)
  @Transactional
  public Assignment createCase(
      String title, String description, User creator, java.time.LocalDateTime deadline) {
    return createCase(title, description, creator, null, deadline);
  }

  @LogActivity(action = ActivityAction.CREATED, entityType = EntityType.ASSIGNMENT, orphan = true)
  @Transactional
  public Assignment createCase(
      String title,
      String description,
      User creator,
      Course course,
      java.time.LocalDateTime deadline) {
    if (course != null
        && course.getEndDate() != null
        && deadline != null
        && deadline.isAfter(course.getEndDate())) {
      throw new org.example.projectbackendteammycodebasebringsalltheboys.exception
          .BadRequestException("Assignment deadline cannot be after course end date.");
    }
    Assignment assignment = new Assignment();
    assignment.setTitle(title);
    assignment.setDescription(description);
    assignment.setCreator(creator);
    assignment.setCourse(course);
    assignment.setDeadline(deadline);

    return assignmentRepository.save(assignment);
  }

  @Transactional(readOnly = true)
  public List<Assignment> getAllCases() {
    return assignmentRepository.findAll();
  }

  @Transactional(readOnly = true)
  public Optional<Assignment> getCaseById(UUID id) {
    return assignmentRepository.findById(id);
  }

  @Transactional(readOnly = true)
  public List<Assignment> getCasesByCreator(User creator) {
    return assignmentRepository.findByCreator(creator);
  }
}
