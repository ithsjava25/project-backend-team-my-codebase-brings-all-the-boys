package org.example.projectbackendteammycodebasebringsalltheboys.controller;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.course.CourseDetailResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.course.CourseSurfaceResponse;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

  private final CourseService courseService;
  private final DtoMapper dtoMapper;
  private final UserService userService;

  @GetMapping
  public ResponseEntity<Page<CourseSurfaceResponse>> getAccessibleCourses(
      @PageableDefault Pageable pageable) {

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated()) {
      throw new UnauthorizedException("User not authenticated");
    }

    String username = auth.getName();
    User user =
        userService
            .getUserByUsername(username)
            .orElseThrow(() -> new UnauthorizedException("Current user not found"));

    Page<Course> courses = courseService.getAccessibleCourses(user, pageable);
    Page<CourseSurfaceResponse> response = courses.map(dtoMapper::toCourseSurfaceResponse);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<CourseDetailResponse> getCourseById(@PathVariable UUID id) {

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated()) {
      throw new UnauthorizedException("User not authenticated");
    }

    String username = auth.getName();
    User user =
        userService
            .getUserByUsername(username)
            .orElseThrow(() -> new UnauthorizedException("Current user not found"));

    return courseService
        .getAccessibleCourse(id, user)
        .map(course -> ResponseEntity.ok(dtoMapper.toCourseDetailResponse(course)))
        .orElse(ResponseEntity.notFound().build());
  }
}
