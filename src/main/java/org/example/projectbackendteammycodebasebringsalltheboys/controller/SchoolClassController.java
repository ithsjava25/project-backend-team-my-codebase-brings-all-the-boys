package org.example.projectbackendteammycodebasebringsalltheboys.controller;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.schoolclass.SchoolClassCreateRequest;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.schoolclass.SchoolClassDetailResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.schoolclass.SchoolClassSurfaceResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.schoolclass.SchoolClassUpdateRequest;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.SchoolClass;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.ClassRole;
import org.example.projectbackendteammycodebasebringsalltheboys.exception.NotFoundException;
import org.example.projectbackendteammycodebasebringsalltheboys.exception.UnauthorizedException;
import org.example.projectbackendteammycodebasebringsalltheboys.mapper.DtoMapper;
import org.example.projectbackendteammycodebasebringsalltheboys.service.ClassEnrollmentService;
import org.example.projectbackendteammycodebasebringsalltheboys.service.SchoolClassService;
import org.example.projectbackendteammycodebasebringsalltheboys.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SchoolClassController {

  private final SchoolClassService schoolClassService;
  private final ClassEnrollmentService enrollmentService;
  private final UserService userService;
  private final DtoMapper dtoMapper;

  @GetMapping("/school-classes")
  public ResponseEntity<List<SchoolClassSurfaceResponse>> getAllSchoolClasses(
      java.security.Principal principal) {
    if (principal == null) {
      throw new UnauthorizedException("Authentication is required");
    }

    User currentUser =
        userService
            .getUserByUsername(principal.getName())
            .orElseThrow(() -> new UnauthorizedException("Current user not found"));

    return ResponseEntity.ok(schoolClassService.getAccessibleSchoolClassesDto(currentUser));
  }

  @GetMapping("/school-classes/{id}")
  public ResponseEntity<SchoolClassDetailResponse> getSchoolClassById(
      @PathVariable UUID id, java.security.Principal principal) {

    if (principal == null) {
      throw new UnauthorizedException("Authentication is required");
    }

    User currentUser =
        userService
            .getUserByUsername(principal.getName())
            .orElseThrow(() -> new UnauthorizedException("Current user not found"));

    return ResponseEntity.ok(schoolClassService.getSchoolClassDetailDto(id, currentUser));
  }

  // Admin endpoints
  @PostMapping("/admin/school-classes")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SchoolClassSurfaceResponse> createSchoolClass(
      @Valid @RequestBody SchoolClassCreateRequest request) {
    SchoolClass sc =
        schoolClassService.createSchoolClass(request.getName(), request.getDescription());
    return ResponseEntity.ok(dtoMapper.toSchoolClassSurfaceResponse(sc));
  }

  @PutMapping("/admin/school-classes/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SchoolClassSurfaceResponse> updateSchoolClass(
      @PathVariable UUID id, @Valid @RequestBody SchoolClassUpdateRequest request) {
    SchoolClass sc =
        schoolClassService.updateSchoolClass(id, request.getName(), request.getDescription());
    return ResponseEntity.ok(dtoMapper.toSchoolClassSurfaceResponse(sc));
  }

  @DeleteMapping("/admin/school-classes/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> deleteSchoolClass(@PathVariable UUID id) {
    schoolClassService.deleteSchoolClass(id);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/admin/school-classes/{id}/enroll")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> enrollUser(
      @PathVariable UUID id,
      @RequestParam UUID userId,
      @RequestParam ClassRole role,
      java.security.Principal principal) {
    User actor =
        userService
            .getUserByUsername(principal.getName())
            .orElseThrow(() -> new UnauthorizedException("Actor not found"));
    SchoolClass sc =
        schoolClassService
            .getSchoolClassById(id)
            .orElseThrow(() -> new NotFoundException("Class not found"));
    User user =
        userService.getUserById(userId).orElseThrow(() -> new NotFoundException("User not found"));

    enrollmentService.enrollUser(user, sc, role, actor);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/admin/school-classes/{id}/enroll/{userId}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> removeEnrollment(
      @PathVariable UUID id, @PathVariable UUID userId, java.security.Principal principal) {
    User actor =
        userService
            .getUserByUsername(principal.getName())
            .orElseThrow(() -> new UnauthorizedException("Actor not found"));
    SchoolClass sc =
        schoolClassService
            .getSchoolClassById(id)
            .orElseThrow(() -> new NotFoundException("Class not found"));
    User user =
        userService.getUserById(userId).orElseThrow(() -> new NotFoundException("User not found"));

    enrollmentService.removeEnrollment(sc, user, actor);
    return ResponseEntity.ok().build();
  }
}
