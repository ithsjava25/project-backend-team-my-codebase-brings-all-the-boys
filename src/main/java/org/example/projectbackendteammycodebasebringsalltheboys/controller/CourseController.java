package org.example.projectbackendteammycodebasebringsalltheboys.controller;

import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.course.CourseCreateRequest;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.course.CourseDetailResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.course.CourseSurfaceResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.course.CourseUpdateRequest;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Course;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.exception.UnauthorizedException;
import org.example.projectbackendteammycodebasebringsalltheboys.mapper.DtoMapper;
import org.example.projectbackendteammycodebasebringsalltheboys.service.CourseService;
import org.example.projectbackendteammycodebasebringsalltheboys.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CourseController {

  private final CourseService courseService;
  private final DtoMapper dtoMapper;
  private final UserService userService;
  private final org.example.projectbackendteammycodebasebringsalltheboys.service.SchoolClassService
      schoolClassService;

  @GetMapping("/courses")
  public ResponseEntity<Page<CourseSurfaceResponse>> getAccessibleCourses(
      @PageableDefault Pageable pageable) {
    User user = getCurrentUser();
    Page<CourseSurfaceResponse> response = courseService.getAccessibleCoursesDto(user, pageable);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/courses/{id}")
  public ResponseEntity<CourseDetailResponse> getCourseById(@PathVariable UUID id) {
    User user = getCurrentUser();
    return courseService
        .getAccessibleCourseDto(id, user)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/admin/courses")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Page<CourseSurfaceResponse>> getAllCoursesAdmin(
      @PageableDefault(sort = "name") Pageable pageable) {
    Page<Course> courses = courseService.getAllCourses(pageable);
    return ResponseEntity.ok(courses.map(dtoMapper::toCourseSurfaceResponse));
  }

  @PostMapping("/admin/courses")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<CourseDetailResponse> createCourseAdmin(
      @Valid @RequestBody CourseCreateRequest request) {
    return createCourse(request);
  }

  @GetMapping("/admin/courses/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<CourseDetailResponse> getCourseAdmin(@PathVariable UUID id) {
    return courseService
        .getCourseById(id)
        .map(dtoMapper::toCourseDetailResponse)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PutMapping("/admin/courses/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<CourseDetailResponse> updateCourseAdmin(
      @PathVariable UUID id, @Valid @RequestBody CourseUpdateRequest request) {
    return updateCourse(id, request);
  }

  @DeleteMapping("/admin/courses/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> deleteCourseAdmin(@PathVariable UUID id) {
    return deleteCourse(id);
  }

  @PostMapping("/courses")
  @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
  public ResponseEntity<CourseDetailResponse> createCourse(
      @Valid @RequestBody CourseCreateRequest request) {
    User creator = getCurrentUser();

    org.example.projectbackendteammycodebasebringsalltheboys.entity.SchoolClass schoolClass =
        schoolClassService
            .getSchoolClassById(request.getSchoolClassId())
            .orElseThrow(
                () ->
                    new org.example.projectbackendteammycodebasebringsalltheboys.exception
                        .NotFoundException("School Class not found"));

    org.example.projectbackendteammycodebasebringsalltheboys.entity.User leadTeacher = null;
    if (request.getLeadTeacherId() != null) {
      leadTeacher =
          userService
              .getUserById(request.getLeadTeacherId())
              .orElseThrow(
                  () ->
                      new org.example.projectbackendteammycodebasebringsalltheboys.exception
                          .NotFoundException("Lead Teacher not found"));
    }

    org.example.projectbackendteammycodebasebringsalltheboys.entity.Course course =
        courseService.createCourse(
            request.getName(),
            request.getDescription(),
            schoolClass,
            leadTeacher,
            creator,
            request.getEndDate());

    return ResponseEntity.ok(dtoMapper.toCourseDetailResponse(course));
  }

  @PutMapping("/courses/{id}")
  @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
  public ResponseEntity<CourseDetailResponse> updateCourse(
      @PathVariable UUID id, @Valid @RequestBody CourseUpdateRequest request) {
    User updater = getCurrentUser();
    return ResponseEntity.ok(courseService.updateCourse(id, request, updater));
  }

  @DeleteMapping("/courses/{id}")
  @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
  public ResponseEntity<Void> deleteCourse(@PathVariable UUID id) {
    User updater = getCurrentUser();
    courseService.deleteCourse(id, updater);
    return ResponseEntity.noContent().build();
  }

  private User getCurrentUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated()) {
      throw new UnauthorizedException("User not authenticated");
    }

    String username = auth.getName();
    return userService
        .getUserByUsername(username)
        .orElseThrow(() -> new UnauthorizedException("Current user not found"));
  }
}
