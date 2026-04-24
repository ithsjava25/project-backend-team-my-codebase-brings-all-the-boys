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
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user-assignments")
@RequiredArgsConstructor
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

    return ResponseEntity.ok(
        userAssignmentRepository.findByAssignment(assignment).stream()
            .map(dtoMapper::toUserAssignmentResponse)
            .collect(Collectors.toList()));
  }

  @GetMapping("/evaluated")
  @PreAuthorize("hasAnyAuthority('ROLE_TEACHER', 'ROLE_ADMIN')")
  public ResponseEntity<List<UserAssignmentResponse>> getEvaluatedAssignments(Pageable pageable) {
    User currentUser = userService.getCurrentUser();
    List<UserAssignment> evaluated =
        userAssignmentService.getEvaluatedAssignmentsForTeacher(currentUser, pageable);
    List<UserAssignmentResponse> response =
        evaluated.stream().map(dtoMapper::toUserAssignmentResponse).collect(Collectors.toList());
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

    userAssignmentService.submitWork(
        ua,
        request.getContent(),
        request.getFileS3Keys() != null ? request.getFileS3Keys() : List.of());

    return ResponseEntity.ok(dtoMapper.toUserAssignmentResponse(ua));
  }
}
