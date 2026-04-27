package org.example.projectbackendteammycodebasebringsalltheboys.controller;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.assignment.EvaluationRequest;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.assignment.SubmissionRequest;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.assignment.UserAssignmentResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Assignment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.UserAssignment;
import org.example.projectbackendteammycodebasebringsalltheboys.exception.ForbiddenException;
import org.example.projectbackendteammycodebasebringsalltheboys.exception.NotFoundException;
import org.example.projectbackendteammycodebasebringsalltheboys.mapper.DtoMapper;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.AssignmentRepository;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.UserAssignmentRepository;
import org.example.projectbackendteammycodebasebringsalltheboys.service.AuthorizationService;
import org.example.projectbackendteammycodebasebringsalltheboys.service.UserAssignmentService;
import org.example.projectbackendteammycodebasebringsalltheboys.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user-assignments")
@RequiredArgsConstructor
@lombok.extern.slf4j.Slf4j
public class UserAssignmentController {

  private final UserAssignmentService userAssignmentService;
  private final UserAssignmentRepository userAssignmentRepository;
  private final AssignmentRepository assignmentRepository;
  private final UserService userService;
  private final AuthorizationService authorizationService;
  private final DtoMapper dtoMapper;

  @GetMapping("/assignment/{assignmentId}")
  @PreAuthorize("hasAnyAuthority('ROLE_TEACHER', 'ROLE_ADMIN')")
  public ResponseEntity<List<UserAssignmentResponse>> getByAssignment(
      @PathVariable UUID assignmentId) {
    Assignment assignment =
        assignmentRepository
            .findById(assignmentId)
            .orElseThrow(() -> new NotFoundException("Assignment not found"));

    User currentUser = userService.getCurrentUser();

    if (!authorizationService.canViewAssignment(currentUser, assignment)) {
      throw new ForbiddenException(
          "You are not authorized to view submissions for this assignment.");
    }

    return ResponseEntity.ok(
        userAssignmentRepository.findByAssignment(assignment).stream()
            .map(dtoMapper::toUserAssignmentResponse)
            .collect(Collectors.toList()));
  }

  @GetMapping("/assignment/{assignmentId}/student/{studentId}")
  @PreAuthorize("hasAnyAuthority('ROLE_TEACHER', 'ROLE_ADMIN')")
  public ResponseEntity<UserAssignmentResponse> getByAssignmentAndStudent(
      @PathVariable UUID assignmentId, @PathVariable UUID studentId) {
    Assignment assignment =
        assignmentRepository
            .findById(assignmentId)
            .orElseThrow(() -> new NotFoundException("Assignment not found"));
    User student =
        userService
            .getUserById(studentId)
            .orElseThrow(() -> new NotFoundException("User not found"));

    User currentUser = userService.getCurrentUser();

    if (!authorizationService.canViewAssignment(currentUser, assignment)) {
      throw new ForbiddenException("You are not authorized to view this assignment.");
    }

    UserAssignment ua =
        userAssignmentService
            .getByAssignmentAndStudent(assignment, student)
            .orElseThrow(() -> new NotFoundException("Submission not found for this student."));

    UserAssignmentResponse response = dtoMapper.toUserAssignmentResponse(ua);
    log.info(
        "Returning UserAssignmentResponse with {} submissions for student {}",
        response.getSubmissions() != null ? response.getSubmissions().size() : 0,
        student.getUsername());

    return ResponseEntity.ok(response);
  }

  @GetMapping("/evaluated")
  @PreAuthorize("hasAnyAuthority('ROLE_TEACHER', 'ROLE_ADMIN')")
  public ResponseEntity<Page<UserAssignmentResponse>> getEvaluatedAssignments(Pageable pageable) {
    User currentUser = userService.getCurrentUser();

    Pageable effectivePageable = pageable;
    if (pageable.getPageSize() > 100) {
      effectivePageable =
          org.springframework.data.domain.PageRequest.of(
              pageable.getPageNumber(), 100, pageable.getSort());
    }

    Page<UserAssignment> evaluated =
        userAssignmentService.getEvaluatedAssignmentsForTeacher(currentUser, effectivePageable);
    Page<UserAssignmentResponse> response = evaluated.map(dtoMapper::toUserAssignmentResponse);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/my/{assignmentId}")
  @PreAuthorize("hasAuthority('ROLE_STUDENT')")
  public ResponseEntity<UserAssignmentResponse> getMyAssignment(@PathVariable UUID assignmentId) {
    User currentUser = userService.getCurrentUser();
    Assignment assignment =
        assignmentRepository
            .findById(assignmentId)
            .orElseThrow(() -> new NotFoundException("Assignment not found"));

    UserAssignment ua = userAssignmentService.getOrCreateForStudent(assignment, currentUser);

    return ResponseEntity.ok(dtoMapper.toUserAssignmentResponse(ua));
  }

  @PostMapping("/{id}/evaluate")
  @PreAuthorize("hasAnyAuthority('ROLE_TEACHER', 'ROLE_ADMIN')")
  public ResponseEntity<UserAssignmentResponse> evaluateAssignment(
      @PathVariable UUID id, @Valid @RequestBody EvaluationRequest request) {
    UserAssignment ua =
        userAssignmentRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("UserAssignment not found"));

    User evaluator = userService.getCurrentUser();

    if (!authorizationService.canModifyCourse(evaluator, ua.getAssignment().getCourse())) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    userAssignmentService.evaluateAssignment(
        ua, request.getGrade(), request.getFeedback(), evaluator);

    return ResponseEntity.ok(dtoMapper.toUserAssignmentResponse(ua));
  }

  @PostMapping("/{id}/submit")
  @PreAuthorize("hasAuthority('ROLE_STUDENT')")
  public ResponseEntity<UserAssignmentResponse> submitWork(
      @PathVariable UUID id, @Valid @RequestBody SubmissionRequest request) {
    UserAssignment ua =
        userAssignmentRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("UserAssignment not found"));

    User currentUser = userService.getCurrentUser();
    if (!ua.getStudent().getId().equals(currentUser.getId())) {
      throw new ForbiddenException("You can only submit your own assignments");
    }

    userAssignmentService.submitWork(ua, request.getContent(), request.getFileS3Keys());

    return ResponseEntity.ok(dtoMapper.toUserAssignmentResponse(ua));
  }
}
