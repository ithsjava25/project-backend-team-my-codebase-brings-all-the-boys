package org.example.projectbackendteammycodebasebringsalltheboys.controller;

import jakarta.validation.Valid;
import java.security.Principal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.schoolclass.SchoolClassCreateRequest;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.schoolclass.SchoolClassDetailResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.schoolclass.SchoolClassUpdateRequest;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.SchoolClass;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.ClassRole;
import org.example.projectbackendteammycodebasebringsalltheboys.exception.NotFoundException;
import org.example.projectbackendteammycodebasebringsalltheboys.security.CurrentUserResolver;
import org.example.projectbackendteammycodebasebringsalltheboys.service.ClassEnrollmentService;
import org.example.projectbackendteammycodebasebringsalltheboys.service.SchoolClassService;
import org.example.projectbackendteammycodebasebringsalltheboys.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/school-classes/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminSchoolClassController {
  private final SchoolClassService schoolClassService;
  private final UserService userService;
  private final ClassEnrollmentService enrollmentService;
  private final CurrentUserResolver currentUserResolver;

  @PostMapping
  public ResponseEntity<SchoolClassDetailResponse> createSchoolClass(
      @Valid @RequestBody SchoolClassCreateRequest request) {
    SchoolClassDetailResponse response = schoolClassService.createSchoolClassDto(request);

    java.net.URI location =
        org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(response.getId())
            .toUri();

    return ResponseEntity.created(location).body(response);
  }

  @PutMapping("/{id}")
  public ResponseEntity<SchoolClassDetailResponse> updateSchoolClass(
      @PathVariable UUID id, @Valid @RequestBody SchoolClassUpdateRequest request) {
    return ResponseEntity.ok(schoolClassService.updateSchoolClassDto(id, request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteSchoolClass(@PathVariable UUID id) {
    schoolClassService.deleteSchoolClass(id);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/{classId}/enroll")
  public ResponseEntity<Void> enrollUser(
      @PathVariable UUID classId,
      @RequestParam UUID userId,
      @RequestParam ClassRole role,
      Principal principal) {

    LookupResult lookup = resolveActorClassUser(principal, classId, userId);
    enrollmentService.enrollUser(lookup.user(), lookup.schoolClass(), role, lookup.actor());
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{classId}/enroll/{userId}")
  public ResponseEntity<Void> removeEnrollment(
      @PathVariable UUID classId, @PathVariable UUID userId, Principal principal) {

    LookupResult lookup = resolveActorClassUser(principal, classId, userId);
    enrollmentService.removeEnrollment(lookup.schoolClass(), lookup.user(), lookup.actor());
    return ResponseEntity.noContent().build();
  }

  private record LookupResult(User actor, SchoolClass schoolClass, User user) {}

  private LookupResult resolveActorClassUser(Principal principal, UUID classId, UUID userId) {
    User actor = currentUserResolver.resolveCurrentUser(principal);

    SchoolClass sc =
        schoolClassService
            .getSchoolClassById(classId)
            .orElseThrow(() -> new NotFoundException("School class not found"));

    User user =
        userService.getUserById(userId).orElseThrow(() -> new NotFoundException("User not found"));

    return new LookupResult(actor, sc, user);
  }
}
