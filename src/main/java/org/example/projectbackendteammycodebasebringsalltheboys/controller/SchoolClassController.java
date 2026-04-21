package org.example.projectbackendteammycodebasebringsalltheboys.controller;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.schoolclass.SchoolClassDetailResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.schoolclass.SchoolClassSurfaceResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.exception.UnauthorizedException;
import org.example.projectbackendteammycodebasebringsalltheboys.mapper.DtoMapper;
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
  private final UserService userService;
  private final DtoMapper dtoMapper;

  @GetMapping("/school-classes")
  public ResponseEntity<List<SchoolClassSurfaceResponse>> getAllSchoolClasses() {
    return ResponseEntity.ok(schoolClassService.getAllSchoolClassesDto());
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
      @RequestBody
          org.example.projectbackendteammycodebasebringsalltheboys.dto.schoolclass
                  .SchoolClassSurfaceResponse
              request) {
    org.example.projectbackendteammycodebasebringsalltheboys.entity.SchoolClass sc =
        schoolClassService.createSchoolClass(request.getName(), request.getDescription());
    return ResponseEntity.ok(dtoMapper.toSchoolClassSurfaceResponse(sc));
  }

  @PutMapping("/admin/school-classes/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SchoolClassSurfaceResponse> updateSchoolClass(
      @PathVariable UUID id,
      @RequestBody
          org.example.projectbackendteammycodebasebringsalltheboys.dto.schoolclass
                  .SchoolClassSurfaceResponse
              request) {
    org.example.projectbackendteammycodebasebringsalltheboys.entity.SchoolClass sc =
        schoolClassService.updateSchoolClass(id, request.getName(), request.getDescription());
    return ResponseEntity.ok(dtoMapper.toSchoolClassSurfaceResponse(sc));
  }

  @DeleteMapping("/admin/school-classes/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> deleteSchoolClass(@PathVariable UUID id) {
    schoolClassService.deleteSchoolClass(id);
    return ResponseEntity.noContent().build();
  }
}
