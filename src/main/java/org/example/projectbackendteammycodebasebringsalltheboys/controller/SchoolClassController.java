package org.example.projectbackendteammycodebasebringsalltheboys.controller;

import java.security.Principal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.schoolclass.SchoolClassDetailResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.schoolclass.SchoolClassSurfaceResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.security.CurrentUserResolver;
import org.example.projectbackendteammycodebasebringsalltheboys.service.SchoolClassService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/school-classes")
@RequiredArgsConstructor
public class SchoolClassController {

  private final SchoolClassService schoolClassService;
  private final CurrentUserResolver currentUserResolver;

  @GetMapping
  public ResponseEntity<Page<SchoolClassSurfaceResponse>> getAccessibleSchoolClasses(
      Principal principal, @PageableDefault(page = 0, size = 20) Pageable pageable) {
    User currentUser = currentUserResolver.resolveCurrentUser(principal);

    Pageable effectivePageable = pageable;
    if (pageable.getPageSize() > 100) {
      effectivePageable = PageRequest.of(pageable.getPageNumber(), 100, pageable.getSort());
    }

    return ResponseEntity.ok(
        schoolClassService.getAccessibleSchoolClassesDto(currentUser, effectivePageable));
  }

  @GetMapping("/{id}")
  public ResponseEntity<SchoolClassDetailResponse> getSchoolClassById(
      @PathVariable UUID id, Principal principal) {
    User currentUser = currentUserResolver.resolveCurrentUser(principal);

    return ResponseEntity.ok(schoolClassService.getSchoolClassDetailDto(id, currentUser));
  }
}
