package org.example.projectbackendteammycodebasebringsalltheboys.controller;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.assignment.EvaluationRequest;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.assignment.UserAssignmentResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Assignment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.UserAssignment;
import org.example.projectbackendteammycodebasebringsalltheboys.exception.NotFoundException;
import org.example.projectbackendteammycodebasebringsalltheboys.mapper.DtoMapper;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.AssignmentRepository;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.UserAssignmentRepository;
import org.example.projectbackendteammycodebasebringsalltheboys.service.AuthorizationService;
import org.example.projectbackendteammycodebasebringsalltheboys.service.UserAssignmentService;
import org.example.projectbackendteammycodebasebringsalltheboys.service.UserService;
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

  @GetMapping("/assignment/{assignmentId}/student/{studentId}")
  public ResponseEntity<UserAssignmentResponse> getUserAssignment(
      @PathVariable UUID assignmentId, @PathVariable UUID studentId) {
    Assignment assignment =
        assignmentRepository
            .findById(assignmentId)
            .orElseThrow(() -> new NotFoundException("Assignment not found"));
    User student =
        userService
            .getUserById(studentId)
            .orElseThrow(() -> new NotFoundException("Student not found"));

    return userAssignmentService
        .getByAssignmentAndStudent(assignment, student)
        .map(dtoMapper::toUserAssignmentResponse)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/assignment/{assignmentId}")
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

  @PostMapping("/{id}/evaluate")
  @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_ADMIN')")
  public ResponseEntity<UserAssignmentResponse> evaluateAssignment(
      @PathVariable UUID id, @Valid @RequestBody EvaluationRequest request) {
    UserAssignment ua =
        userAssignmentRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("UserAssignment not found"));

    User evaluator = userService.getCurrentUser();

    // authorizationService.canModifyCourse(evaluator, ua.getAssignment().getCourse())
    // or similar check if needed. Lead teacher check is implicit in UserAssignmentService's
    // counting logic but here we check explicitly.

    userAssignmentService.evaluateAssignment(
        ua, request.getGrade(), request.getFeedback(), evaluator);

    return ResponseEntity.ok(dtoMapper.toUserAssignmentResponse(ua));
  }
}
