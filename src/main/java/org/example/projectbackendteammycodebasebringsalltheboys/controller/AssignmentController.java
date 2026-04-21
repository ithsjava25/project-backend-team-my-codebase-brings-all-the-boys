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
  public ResponseEntity<AssignmentDetailResponse> createAssignment(
      @Valid @RequestBody CaseRequest request, Principal principal) {

    User currentUser = getCurrentUser(principal);
    AssignmentDetailResponse response = caseService.createCase(request, currentUser);

    URI location =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(response.getId())
            .toUri();

    return ResponseEntity.created(location).body(response);
  }

  @GetMapping
  public ResponseEntity<List<AssignmentResponse>> getAllAssignments(Principal principal) {
    User currentUser = getCurrentUser(principal);
    return ResponseEntity.ok(caseService.getAccessibleAssignments(currentUser));
  }

  @GetMapping("/{id}")
  public ResponseEntity<AssignmentDetailResponse> getAssignmentById(
      @PathVariable UUID id, Principal principal) {
    User currentUser = getCurrentUser(principal);
    return caseService
        .getAccessibleAssignmentDetail(id, currentUser)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
  public ResponseEntity<AssignmentDetailResponse> updateAssignment(
      @PathVariable UUID id,
      @RequestBody
          org.example.projectbackendteammycodebasebringsalltheboys.dto.assignment
                  .AssignmentUpdateRequest
              request,
      Principal principal) {
    User currentUser = getCurrentUser(principal);
    return ResponseEntity.ok(caseService.updateAssignment(id, request, currentUser));
  }

  @GetMapping("/my-created")
  @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
  public ResponseEntity<List<CaseResponse>> getMyCreatedAssignments(Principal principal) {
    User currentUser = getCurrentUser(principal);

    List<Assignment> assignments = caseService.getCasesByCreator(currentUser);
    List<CaseResponse> response =
        assignments.stream().map(dtoMapper::toCaseResponse).collect(Collectors.toList());
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
  public ResponseEntity<Void> deleteAssignment(@PathVariable UUID id, Principal principal) {
    User currentUser = getCurrentUser(principal);
    caseService.deleteAssignment(id, currentUser);
    return ResponseEntity.noContent().build();
  }

  private User getCurrentUser(Principal principal) {
    if (principal == null) {
      throw new UnauthorizedException("Authentication is required");
    }
    return userService
        .getUserByUsername(principal.getName())
        .orElseThrow(() -> new UnauthorizedException("Current user not found"));
  }
}
