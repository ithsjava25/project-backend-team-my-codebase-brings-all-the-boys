package org.example.projectbackendteammycodebasebringsalltheboys.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.schoolclass.SchoolClassDetailResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.schoolclass.SchoolClassSurfaceResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.SchoolClass;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.exception.ForbiddenException;
import org.example.projectbackendteammycodebasebringsalltheboys.exception.NotFoundException;
import org.example.projectbackendteammycodebasebringsalltheboys.exception.UnauthorizedException;
import org.example.projectbackendteammycodebasebringsalltheboys.mapper.DtoMapper;
import org.example.projectbackendteammycodebasebringsalltheboys.service.SchoolClassService;
import org.example.projectbackendteammycodebasebringsalltheboys.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/school-classes")
@RequiredArgsConstructor
public class SchoolClassController {

    private final SchoolClassService schoolClassService;
    private final UserService userService;
    private final DtoMapper dtoMapper;

    @GetMapping
    public ResponseEntity<List<SchoolClassSurfaceResponse>>
    getAllSchoolClasses() { // Assuming SchoolClassSurfaceResponse exists
        List<SchoolClass> schoolClasses = schoolClassService.getAllClasses();
        List<SchoolClassSurfaceResponse> response =
                schoolClasses.stream()
                        .map(dtoMapper::toSchoolClassSurfaceResponse)
                        .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @Transactional(readOnly = true)
    @GetMapping("/{id}")
    public ResponseEntity<SchoolClassDetailResponse> getSchoolClassById(
            @PathVariable UUID id,
            java.security.Principal principal) { // Assuming SchoolClassDetailResponse exists

        if (principal == null) {
            throw new UnauthorizedException("Authentication is required");
        }

        User currentUser =
                userService
                        .getUserByUsername(principal.getName())
                        .orElseThrow(() -> new UnauthorizedException("Current user not found"));

        SchoolClass schoolClass =
                schoolClassService
                        .getClassById(id)
                        .orElseThrow(() -> new NotFoundException("School class not found with id: " + id));

        // Authorization check: Allow Admins, Teachers, Mentors, and enrolled Students to view details
        boolean isTeacherOrAdmin =
                currentUser.getRole().getName().equals("ROLE_ADMIN")
                        || currentUser.getRole().getName().equals("ROLE_TEACHER");
        boolean isMentor =
                schoolClass.getEnrollments().stream()
                        .anyMatch(
                                e ->
                                        e.getUser().getId().equals(currentUser.getId())
                                                && e.getClassRole()
                                                == org.example.projectbackendteammycodebasebringsalltheboys.enums
                                                .ClassRole.MENTOR);
        boolean isEnrolledStudent =
                schoolClass.getEnrollments().stream()
                        .anyMatch(
                                e ->
                                        e.getUser().getId().equals(currentUser.getId())
                                                && e.getClassRole()
                                                == org.example.projectbackendteammycodebasebringsalltheboys.enums
                                                .ClassRole.STUDENT);

        if (isTeacherOrAdmin || isMentor || isEnrolledStudent) {
            return ResponseEntity.ok(
                    dtoMapper.toSchoolClassDetailResponse(schoolClass)); // Corrected mapping
        } else {
            // For users who are not authorized, deny access.
            throw new ForbiddenException(
                    "You do not have permission to view this school class's details.");
        }
    }
}
