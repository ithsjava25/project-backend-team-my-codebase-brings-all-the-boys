package org.example.projectbackendteammycodebasebringsalltheboys.controller;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.course.CourseSurfaceResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.course.CourseDetailResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Course;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.mapper.DtoMapper;
import org.example.projectbackendteammycodebasebringsalltheboys.service.CourseService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final DtoMapper dtoMapper;

    @GetMapping
    public ResponseEntity<List<CourseSurfaceResponse>> getAccessibleCourses(
            @AuthenticationPrincipal User user
    ) {
        List<Course> courses = courseService.getAccessibleCourses(user);
        List<CourseSurfaceResponse> response = courses.stream()
                .map(dtoMapper::toCourseSurfaceResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseDetailResponse> getCourseById(
            @PathVariable UUID id,
            @AuthenticationPrincipal User user
    ) {
        return courseService.getAccessibleCourse(id, user)
                .map(course -> ResponseEntity.ok(dtoMapper.toCourseDetailResponse(course)))
                .orElse(ResponseEntity.notFound().build());
    }
}