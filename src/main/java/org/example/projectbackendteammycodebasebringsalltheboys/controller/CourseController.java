package org.example.projectbackendteammycodebasebringsalltheboys.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.course.CourseDetailResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.course.CourseSurfaceResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Course;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.exception.ForbiddenException;
import org.example.projectbackendteammycodebasebringsalltheboys.exception.NotFoundException;
import org.example.projectbackendteammycodebasebringsalltheboys.exception.UnauthorizedException;
import org.example.projectbackendteammycodebasebringsalltheboys.mapper.DtoMapper;
import org.example.projectbackendteammycodebasebringsalltheboys.service.CourseService;
import org.example.projectbackendteammycodebasebringsalltheboys.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final UserService userService;
    private final DtoMapper dtoMapper;

    @GetMapping
    public ResponseEntity<List<CourseSurfaceResponse>> getAllCourses() {
        List<Course> courses = courseService.getAllCourses();
        List<CourseSurfaceResponse> response =
                courses.stream().map(dtoMapper::toCourseSurfaceResponse).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseDetailResponse> getCourseById(
            @PathVariable UUID id, java.security.Principal principal) {
        if (principal == null) {
            throw new UnauthorizedException("Authentication is required");
        }

        User currentUser =
                userService
                        .getUserByUsername(principal.getName())
                        .orElseThrow(() -> new UnauthorizedException("Current user not found"));

        Course course =
                courseService
                        .getCourseById(id)
                        .orElseThrow(() -> new NotFoundException("Course not found with id: " + id));

        boolean isTeacherOrAdmin =
                currentUser.getRole().getName().equals("ROLE_ADMIN")
                        || currentUser.getRole().getName().equals("ROLE_TEACHER");
        boolean isLeadTeacher =
                course.getLeadTeacher() != null
                        && course.getLeadTeacher().getId().equals(currentUser.getId());
        boolean isAssistant =
                course.getAssistants() != null && course.getAssistants()
                        .stream().anyMatch(assistant -> assistant.getId()
                                .equals(currentUser.getId()));
        boolean isEnrolledStudent =
                course.getSchoolClass() != null
                        && course.getSchoolClass().getEnrollments().stream()
                        .anyMatch(
                                e ->
                                        e.getUser().getId().equals(currentUser.getId())
                                                && e.getClassRole()
                                                == org.example.projectbackendteammycodebasebringsalltheboys.enums
                                                .ClassRole.STUDENT);

        if (isTeacherOrAdmin || isLeadTeacher || isAssistant || isEnrolledStudent) {
            return ResponseEntity.ok(dtoMapper.toCourseDetailResponse(course));
        } else {
            throw new ForbiddenException("You do not have permission to view this course's details.");
        }
    }
}
