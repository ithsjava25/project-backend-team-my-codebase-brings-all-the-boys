package org.example.projectbackendteammycodebasebringsalltheboys.controller;

import jakarta.validation.Valid;
import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.casefile.CaseRequest;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.casefile.CaseResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Assignment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.mapper.DtoMapper;
import org.example.projectbackendteammycodebasebringsalltheboys.service.CaseService;
import org.example.projectbackendteammycodebasebringsalltheboys.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
public class AssignmentController {

  private final CaseService caseService;
  private final UserService userService;
  private final DtoMapper dtoMapper;

  @PostMapping
  @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
  public ResponseEntity<CaseResponse> createAssignment(
      @Valid @RequestBody CaseRequest request, Principal principal) {

    User currentUser =
        userService
            .getUserByUsername(principal.getName())
            .orElseThrow(
                () ->
                    new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Current user not found"));

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
  public ResponseEntity<List<CaseResponse>> getAllAssignments() {
    List<Assignment> assignments = caseService.getAllCases();
    List<CaseResponse> response =
        assignments.stream().map(dtoMapper::toCaseResponse).collect(Collectors.toList());
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<CaseResponse> getAssignmentById(@PathVariable Long id) {
    return caseService
        .getCaseById(id)
        .map(dtoMapper::toCaseResponse)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/my-created")
  @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
  public ResponseEntity<List<CaseResponse>> getMyCreatedAssignments(Principal principal) {
    User currentUser =
        userService
            .getUserByUsername(principal.getName())
            .orElseThrow(() -> new IllegalStateException("Current user not found"));

    List<Assignment> assignments = caseService.getCasesByCreator(currentUser);
    List<CaseResponse> response =
        assignments.stream().map(dtoMapper::toCaseResponse).collect(Collectors.toList());
    return ResponseEntity.ok(response);
  }
}
