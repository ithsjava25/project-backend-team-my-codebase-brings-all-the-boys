package org.example.projectbackendteammycodebasebringsalltheboys.controller;

import jakarta.validation.Valid;
import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.assignment.AssignmentDetailResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.assignment.AssignmentResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.casefile.CaseRequest;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.casefile.CaseResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Assignment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.exception.ForbiddenException;
import org.example.projectbackendteammycodebasebringsalltheboys.exception.NotFoundException;
import org.example.projectbackendteammycodebasebringsalltheboys.exception.UnauthorizedException;
import org.example.projectbackendteammycodebasebringsalltheboys.mapper.DtoMapper;
import org.example.projectbackendteammycodebasebringsalltheboys.service.AuthorizationService;
import org.example.projectbackendteammycodebasebringsalltheboys.service.CaseService;
import org.example.projectbackendteammycodebasebringsalltheboys.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
public class AssignmentController {

  private final CaseService caseService;
  private final UserService userService;
  private final AuthorizationService authorizationService;
  private final DtoMapper dtoMapper;

  @PostMapping
  @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
  public ResponseEntity<CaseResponse> createAssignment(
      @Valid @RequestBody CaseRequest request, Principal principal) {

    User currentUser =
        userService
            .getUserByUsername(principal.getName())
            .orElseThrow(() -> new UnauthorizedException("Current user not found"));

    Assignment assignment =
        caseService.createCase(request.getTitle(), request.getDescription(), currentUser);

    URI location =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(assignment.getId())
            .toUri();

    return ResponseEntity.created(location).body(dtoMapper.toCaseResponse(assignment));
  }

  @GetMapping
  public ResponseEntity<List<AssignmentResponse>> getAllAssignments(Principal principal) {
    if (principal == null) {
      throw new UnauthorizedException("Authentication is required.");
    }
    User currentUser =
        userService
            .getUserByUsername(principal.getName())
            .orElseThrow(() -> new UnauthorizedException("Current user not found"));

    List<Assignment> assignments = caseService.getAllCases();

    List<AssignmentResponse> response =
        assignments.stream()
            .filter(assignment -> authorizationService.canViewAssignment(currentUser, assignment))
            .map(dtoMapper::toAssignmentResponse)
            .collect(Collectors.toList());

    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<AssignmentDetailResponse> getAssignmentById(
      @PathVariable UUID id, Principal principal) {
    if (principal == null) {
      throw new UnauthorizedException("Authentication is required");
    }

    User currentUser =
        userService
            .getUserByUsername(principal.getName())
            .orElseThrow(() -> new UnauthorizedException("Current user not found"));

    Assignment assignment =
        caseService
            .getCaseById(id)
            .orElseThrow(() -> new NotFoundException("Assignment not found with id: " + id));

    if (!authorizationService.canAccessAssignmentDetails(currentUser, assignment)) {
      throw new ForbiddenException("You do not have permission to view this assignment's details.");
    }

    return ResponseEntity.ok(dtoMapper.toAssignmentDetailResponse(assignment));
  }

  @GetMapping("/my-created")
  @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
  public ResponseEntity<List<CaseResponse>> getMyCreatedAssignments(Principal principal) {
    User currentUser =
        userService
            .getUserByUsername(principal.getName())
            .orElseThrow(() -> new UnauthorizedException("Current user not found"));

    List<Assignment> assignments = caseService.getCasesByCreator(currentUser);
    List<CaseResponse> response =
        assignments.stream().map(dtoMapper::toCaseResponse).collect(Collectors.toList());
    return ResponseEntity.ok(response);
  }
}
