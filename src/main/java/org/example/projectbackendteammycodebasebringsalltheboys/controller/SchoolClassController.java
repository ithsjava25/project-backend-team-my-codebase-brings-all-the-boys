package org.example.projectbackendteammycodebasebringsalltheboys.controller;

import jakarta.validation.Valid;
import java.security.Principal;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/school-classes")
@RequiredArgsConstructor
public class SchoolClassController {

  private static final int MAX_PAGE_SIZE = 100;
  private final SchoolClassService schoolClassService;
  private final UserService userService;
  private final ClassEnrollmentService enrollmentService;

  @GetMapping
  public ResponseEntity<org.springframework.data.domain.Page<SchoolClassSurfaceResponse>>
      getAccessibleSchoolClasses(Principal principal, Pageable pageable) {
    if (principal == null) {
      throw new UnauthorizedException("Authentication is required");
    }

    User currentUser =
        userService
            .getUserByUsername(principal.getName())
            .orElseThrow(() -> new UnauthorizedException("Current user not found"));

    Pageable effectivePageable = pageable;
    if (pageable.getPageSize() > MAX_PAGE_SIZE) {
      effectivePageable =
          PageRequest.of(pageable.getPageNumber(), MAX_PAGE_SIZE, pageable.getSort());
    }

    return ResponseEntity.ok(
        schoolClassService.getAccessibleSchoolClassesDto(currentUser, effectivePageable));
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
}

@RestController
@RequestMapping("/api/admin/school-classes")
@RequiredArgsConstructor
class AdminSchoolClassController {
  private final SchoolClassService schoolClassService;
  private final UserService userService;
  private final ClassEnrollmentService enrollmentService;

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SchoolClassDetailResponse> createSchoolClass(
      @Valid @RequestBody SchoolClassCreateRequest request) {
    return ResponseEntity.ok(schoolClassService.createSchoolClassDto(request));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SchoolClassDetailResponse> updateSchoolClass(
      @PathVariable UUID id, @Valid @RequestBody SchoolClassUpdateRequest request) {
    return ResponseEntity.ok(schoolClassService.updateSchoolClassDto(id, request));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> deleteSchoolClass(@PathVariable UUID id) {
    schoolClassService.deleteSchoolClass(id);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/{classId}/enroll")
  @PreAuthorize("hasRole('ADMIN')")
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

  @DeleteMapping("/{classId}/enroll/{userId}")
  @PreAuthorize("hasRole('ADMIN')")
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
