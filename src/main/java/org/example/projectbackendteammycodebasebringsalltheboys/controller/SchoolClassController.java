package org.example.projectbackendteammycodebasebringsalltheboys.controller;

import java.security.Principal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.schoolclass.SchoolClassDetailResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.schoolclass.SchoolClassSurfaceResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.exception.UnauthorizedException;
import org.example.projectbackendteammycodebasebringsalltheboys.service.SchoolClassService;
import org.example.projectbackendteammycodebasebringsalltheboys.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/school-classes")
@RequiredArgsConstructor
public class SchoolClassController {

  private final SchoolClassService schoolClassService;
  private final UserService userService;

  @GetMapping
  public ResponseEntity<Page<SchoolClassSurfaceResponse>> getAccessibleSchoolClasses(
      Principal principal,
      @PageableDefault(page = 0, size = 20) org.springframework.data.domain.Pageable pageable) {
    User currentUser = requireCurrentUser(principal);

    org.springframework.data.domain.Pageable effectivePageable = pageable;
    if (pageable.getPageSize() > 100) {
      effectivePageable =
          org.springframework.data.domain.PageRequest.of(
              pageable.getPageNumber(), 100, pageable.getSort());
    }

    return ResponseEntity.ok(
        schoolClassService.getAccessibleSchoolClassesDto(currentUser, effectivePageable));
  }

  @GetMapping("/{id}")
  public ResponseEntity<SchoolClassDetailResponse> getSchoolClassById(
      @PathVariable UUID id, Principal principal) {
    User currentUser = requireCurrentUser(principal);

    return ResponseEntity.ok(schoolClassService.getSchoolClassDetailDto(id, currentUser));
  }

  private User requireCurrentUser(Principal principal) {
    if (principal == null) {
      throw new UnauthorizedException("Authentication is required");
    }

    return userService
        .getUserByUsername(principal.getName())
        .orElseThrow(() -> new UnauthorizedException("Current user not found"));
  }
}
