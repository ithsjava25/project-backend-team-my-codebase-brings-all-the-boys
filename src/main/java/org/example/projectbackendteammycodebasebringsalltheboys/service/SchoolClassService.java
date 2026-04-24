package org.example.projectbackendteammycodebasebringsalltheboys.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.schoolclass.SchoolClassCreateRequest;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.schoolclass.SchoolClassDetailResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.schoolclass.SchoolClassSurfaceResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.schoolclass.SchoolClassUpdateRequest;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.SchoolClass;
import org.example.projectbackendteammycodebasebringsalltheboys.exception.NotFoundException;
import org.example.projectbackendteammycodebasebringsalltheboys.mapper.DtoMapper;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.SchoolClassRepository;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SchoolClassService {

  private final SchoolClassRepository schoolClassRepository;
  private final DtoMapper dtoMapper;
  private final ClassEnrollmentService enrollmentService;

  @Transactional(readOnly = true)
  public SchoolClassDetailResponse getSchoolClassDetailDto(
      UUID id, org.example.projectbackendteammycodebasebringsalltheboys.entity.User currentUser) {
    // Fetch sc with courses eagerly loaded
    SchoolClass schoolClass =
        schoolClassRepository
            .findWithCoursesById(id)
            .orElseThrow(() -> new NotFoundException("School class not found with id: " + id));

    // Explicitly initialize the second collection to guarantee loading
    Hibernate.initialize(schoolClass.getEnrollments());

    // Authorization check: Allow Admins, Teachers, Mentors, and enrolled Students to view details
    boolean isAuthorized =
        currentUser.getRole().getName().equals("ROLE_ADMIN")
            || currentUser.getRole().getName().equals("ROLE_TEACHER")
            || enrollmentService.isUserInClass(currentUser, schoolClass);

    if (!isAuthorized) {
      throw new org.example.projectbackendteammycodebasebringsalltheboys.exception
          .ForbiddenException("You are not authorized to view this class.");
    }

    return dtoMapper.toSchoolClassDetailResponse(schoolClass);
  }

  @Transactional(readOnly = true)
  public List<SchoolClassSurfaceResponse> getAccessibleSchoolClassesDto(
      org.example.projectbackendteammycodebasebringsalltheboys.entity.User user,
      Pageable pageable) {
    if (user == null || user.getRole() == null) {
      return Collections.emptyList();
    }
    String roleName = user.getRole().getName();
    List<SchoolClass> classes;

    if (roleName.equals("ROLE_ADMIN") || roleName.equals("ROLE_TEACHER")) {
      classes = schoolClassRepository.findAllWithDetails();
    } else {
      classes = schoolClassRepository.findByUserIdPaged(user.getId(), pageable).getContent();
    }

    return classes.stream()
        .map(dtoMapper::toSchoolClassSurfaceResponse)
        .collect(Collectors.toList());
  }

  @Transactional
  public SchoolClass createSchoolClass(SchoolClassCreateRequest request) {
    SchoolClass sc = new SchoolClass();
    sc.setName(request.getName());
    sc.setDescription(request.getDescription());
    return schoolClassRepository.save(sc);
  }

  @Transactional
  public SchoolClass updateSchoolClass(UUID id, SchoolClassUpdateRequest request) {
    SchoolClass sc =
        schoolClassRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("School class not found"));
    sc.setName(request.getName());
    sc.setDescription(request.getDescription());
    return schoolClassRepository.save(sc);
  }

  @Transactional(readOnly = true)
  public Optional<SchoolClass> getSchoolClassById(UUID id) {
    return schoolClassRepository.findById(id);
  }

  @Transactional(readOnly = true)
  public List<SchoolClass> getAllSchoolClasses() {
    return schoolClassRepository.findAll();
  }

  @Transactional
  public void deleteSchoolClass(UUID id) {
    SchoolClass schoolClass =
        schoolClassRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("School class not found"));

    // Ensure all associated courses and enrollments are also soft-deleted by clearing the
    // collections and letting orphanRemoval = true take care of it.
    schoolClass.getCourses().clear();
    schoolClass.getEnrollments().clear();

    schoolClassRepository.delete(schoolClass);
  }
}
