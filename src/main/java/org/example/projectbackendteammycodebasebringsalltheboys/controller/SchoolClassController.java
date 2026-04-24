package org.example.projectbackendteammycodebasebringsalltheboys.controller;

import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.schoolclass.SchoolClassCreateRequest;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.schoolclass.SchoolClassDetailResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.schoolclass.SchoolClassSurfaceResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.schoolclass.SchoolClassUpdateRequest;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.exception.NotFoundException;
import org.example.projectbackendteammycodebasebringsalltheboys.exception.UnauthorizedException;
import org.example.projectbackendteammycodebasebringsalltheboys.service.ClassEnrollmentService;
import org.example.projectbackendteammycodebasebringsalltheboys.service.SchoolClassService;
import org.example.projectbackendteammycodebasebringsalltheboys.service.UserService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/school-classes")
@RequiredArgsConstructor
public class SchoolClassController {

  private final SchoolClassService schoolClassService;
  private final UserService userService;
  private final ClassEnrollmentService enrollmentService;

  @GetMapping
  public ResponseEntity<List<SchoolClassSurfaceResponse>> getAccessibleSchoolClasses(
      Principal principal, Pageable pageable) {
    if (principal == null) {
      throw new UnauthorizedException("Authentication is required");
    }

    User currentUser =
        userService
            .getUserByUsername(principal.getName())
            .orElseThrow(() -> new UnauthorizedException("Current user not found"));

    return ResponseEntity.ok(
        schoolClassService.getAccessibleSchoolClassesDto(currentUser, pageable));
  }

  @GetMapping("/{id}")
  public ResponseEntity<SchoolClassDetailResponse> getSchoolClassById(
      @PathVariable UUID id, Principal principal) {
    if (principal == null) {
      throw new UnauthorizedException("Authentication is required");
    }

    User currentUser =
        userService
            .getUserByUsername(principal.getName())
            .orElseThrow(() -> new UnauthorizedException("Current user not found"));

    return ResponseEntity.ok(schoolClassService.getSchoolClassDetailDto(id, currentUser));
  }

  @PostMapping("/admin")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<org.example.projectbackendteammycodebasebringsalltheboys.entity.SchoolClass>
      createSchoolClass(@Valid @RequestBody SchoolClassCreateRequest request) {
    return ResponseEntity.ok(schoolClassService.createSchoolClass(request));
  }

  @PutMapping("/admin/{id}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<org.example.projectbackendteammycodebasebringsalltheboys.entity.SchoolClass>
      updateSchoolClass(
          @PathVariable UUID id, @Valid @RequestBody SchoolClassUpdateRequest request) {
    return ResponseEntity.ok(schoolClassService.updateSchoolClass(id, request));
  }

  @DeleteMapping("/admin/{id}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<Void> deleteSchoolClass(@PathVariable UUID id) {
    schoolClassService.deleteSchoolClass(id);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/admin/{classId}/enroll")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<Void> enrollUser(
      @PathVariable UUID classId,
      @RequestParam UUID userId,
      @RequestParam org.example.projectbackendteammycodebasebringsalltheboys.enums.ClassRole role,
      Principal principal) {
    if (principal == null) {
      throw new UnauthorizedException("Authentication is required");
    }

    User actor =
        userService
            .getUserByUsername(principal.getName())
            .orElseThrow(() -> new UnauthorizedException("Current user not found"));

    org.example.projectbackendteammycodebasebringsalltheboys.entity.SchoolClass sc =
        schoolClassService
            .getSchoolClassById(classId)
            .orElseThrow(() -> new NotFoundException("School class not found"));
    User user =
        userService.getUserById(userId).orElseThrow(() -> new NotFoundException("User not found"));

    enrollmentService.enrollUser(user, sc, role, actor);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/admin/{classId}/enroll/{userId}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<Void> removeEnrollment(
      @PathVariable UUID classId, @PathVariable UUID userId, Principal principal) {
    if (principal == null) {
      throw new UnauthorizedException("Authentication is required");
    }

    User actor =
        userService
            .getUserByUsername(principal.getName())
            .orElseThrow(() -> new UnauthorizedException("Current user not found"));

    org.example.projectbackendteammycodebasebringsalltheboys.entity.SchoolClass sc =
        schoolClassService
            .getSchoolClassById(classId)
            .orElseThrow(() -> new NotFoundException("School class not found"));
    User user =
        userService.getUserById(userId).orElseThrow(() -> new NotFoundException("User not found"));

    enrollmentService.removeEnrollment(sc, user, actor);
    return ResponseEntity.ok().build();
  }
}
