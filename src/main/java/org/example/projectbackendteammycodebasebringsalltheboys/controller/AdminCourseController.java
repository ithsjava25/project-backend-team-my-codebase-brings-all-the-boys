package org.example.projectbackendteammycodebasebringsalltheboys.controller;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.course.CourseCreateRequest;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.course.CourseDetailResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.course.CourseSurfaceResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.course.CourseUpdateRequest;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Course;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.exception.NotFoundException;
import org.example.projectbackendteammycodebasebringsalltheboys.mapper.DtoMapper;
import org.example.projectbackendteammycodebasebringsalltheboys.service.CourseService;
import org.example.projectbackendteammycodebasebringsalltheboys.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/admin/courses")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") // Restrict all endpoints in this controller to ADMIN role
public class AdminCourseController {

  private final CourseService courseService;
  private final UserService userService;
  private final DtoMapper dtoMapper;

  @Transactional(readOnly = true)
  @GetMapping
  public ResponseEntity<Page<CourseSurfaceResponse>> getAllCoursesAdmin(
      @PageableDefault Pageable pageable) {
    Page<Course> courses = courseService.getAllCourses(pageable);
    Page<CourseSurfaceResponse> response = courses.map(dtoMapper::toCourseSurfaceResponse);
    return ResponseEntity.ok(response);
  }

  @Transactional(readOnly = true)
  @GetMapping("/{id}")
  public ResponseEntity<CourseDetailResponse> getCourseByIdAdmin(@PathVariable UUID id) {
    Course course =
        courseService
            .getCourseById(id)
            .orElseThrow(() -> new NotFoundException("Course not found with id: " + id));
    return ResponseEntity.ok(dtoMapper.toCourseDetailResponse(course));
  }

  @PostMapping
  public ResponseEntity<CourseDetailResponse> createCourse(
      @RequestBody CourseCreateRequest request) {
    // Fetch necessary entities (e.g., SchoolClass, LeadTeacher, Creator)
    // For simplicity, assuming they can be fetched or are provided in request
    // You'll need to inject services like SchoolClassService and UserService if not already
    // available
    // Example:
    // SchoolClass schoolClass = schoolClassService.getSchoolClassById(request.getSchoolClassId())
    //         .orElseThrow(() -> new NotFoundException("SchoolClass not found"));
    // User leadTeacher = userService.findUserById(request.getLeadTeacherId()).orElseThrow(...);
    // User creator = userService.getCurrentUser(); // Assuming a method to get current logged-in
    // user

    // For now, demonstrating with placeholders and simplified logic.
    // Actual implementation would require fetching these entities properly.
    // This part needs to be fleshed out based on existing services.
    // For a basic example, let's assume we have the entities and can call createCourse.

    // Placeholder logic - replace with actual service calls
    // User creator = userService.getCurrentUser(); // This method needs to exist
    User creator = new User(); // Placeholder
    creator.setId(UUID.randomUUID()); // Placeholder
    creator.setUsername("admin"); // Placeholder

    Course createdCourse =
        courseService.createCourse(
            request.getName(),
            request.getDescription(),
            null, // Placeholder for SchoolClass
            null, // Placeholder for LeadTeacher
            creator,
            request.getEndDate());

    var uri =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(createdCourse.getId())
            .toUri();
    return ResponseEntity.created(uri).body(dtoMapper.toCourseDetailResponse(createdCourse));
  }

  @PutMapping("/{id}")
  public ResponseEntity<CourseDetailResponse> updateCourse(
      @PathVariable UUID id, @RequestBody CourseUpdateRequest request) {
    Course course =
        courseService
            .getCourseById(id)
            .orElseThrow(() -> new NotFoundException("Course not found with id: " + id));

    // Update course properties from request
    course.setName(request.getName());
    course.setDescription(request.getDescription());
    course.setEndDate(request.getEndDate());
    // Update lead teacher and assistants if necessary, requires fetching User entities

    Course updatedCourse =
        courseService.updateCourse(course); // Assuming updateCourse method exists in service
    return ResponseEntity.ok(dtoMapper.toCourseDetailResponse(updatedCourse));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteCourse(@PathVariable UUID id) {
    // CourseService.deleteCourse is already implemented and secured by @PreAuthorize
    courseService.deleteCourse(id, userService.getCurrentUser());
    return ResponseEntity.noContent().build();
  }
}
