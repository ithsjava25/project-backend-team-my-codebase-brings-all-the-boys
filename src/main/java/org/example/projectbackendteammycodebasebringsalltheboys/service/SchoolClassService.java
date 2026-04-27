package org.example.projectbackendteammycodebasebringsalltheboys.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
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

    // Guard for null currentUser or null role
    String roleName =
        (currentUser == null || currentUser.getRole() == null)
            ? null
            : currentUser.getRole().getName();

    // Authorization check: Allow Admins, Teachers, Mentors, and enrolled Students to view details
    boolean isAuthorized =
        "ROLE_ADMIN".equals(roleName)
            || "ROLE_TEACHER".equals(roleName)
            || (currentUser != null && enrollmentService.isUserInClass(currentUser, schoolClass));

    if (!isAuthorized) {
      throw new org.example.projectbackendteammycodebasebringsalltheboys.exception
          .ForbiddenException("You are not authorized to view this class.");
    }

    return dtoMapper.toSchoolClassDetailResponse(schoolClass);
  }

  @Transactional(readOnly = true)
  public org.springframework.data.domain.Page<SchoolClassSurfaceResponse>
      getAccessibleSchoolClassesDto(
          org.example.projectbackendteammycodebasebringsalltheboys.entity.User user,
          Pageable pageable) {
    if (user == null || user.getRole() == null) {
      return org.springframework.data.domain.Page.empty(pageable);
    }
    String roleName = user.getRole().getName();
    org.springframework.data.domain.Page<SchoolClass> classes;

    if (roleName.equals("ROLE_ADMIN") || roleName.equals("ROLE_TEACHER")) {
      // Return simple paged list for privileged users
      classes = schoolClassRepository.findAll(pageable);
    } else {
      classes = schoolClassRepository.findByUserIdPaged(user.getId(), pageable);
    }

    return classes.map(dtoMapper::toSchoolClassSurfaceResponse);
  }

  @Transactional
  public SchoolClass createSchoolClass(SchoolClassCreateRequest request) {
    if (schoolClassRepository.findByName(request.getName()).isPresent()) {
      throw new org.example.projectbackendteammycodebasebringsalltheboys.exception
          .BadRequestException("Class with name '" + request.getName() + "' already exists");
    }
    SchoolClass sc = new SchoolClass();
    sc.setName(request.getName());
    sc.setDescription(request.getDescription());
    return schoolClassRepository.save(sc);
  }

  @Transactional
  public SchoolClassDetailResponse createSchoolClassDto(SchoolClassCreateRequest request) {
    return dtoMapper.toSchoolClassDetailResponse(createSchoolClass(request));
  }

  @Transactional
  public SchoolClass updateSchoolClass(UUID id, SchoolClassUpdateRequest request) {
    SchoolClass sc =
        schoolClassRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("School class not found"));

    if (request.getName() != null
        && !request.getName().equals(sc.getName())
        && schoolClassRepository.findByName(request.getName()).isPresent()) {
      throw new org.example.projectbackendteammycodebasebringsalltheboys.exception
          .BadRequestException("Class with name '" + request.getName() + "' already exists");
    }

    if (request.getName() != null) {
      sc.setName(request.getName());
    }
    if (request.getDescription() != null) {
      sc.setDescription(request.getDescription());
    }
    return schoolClassRepository.save(sc);
  }

  @Transactional
  public SchoolClassDetailResponse updateSchoolClassDto(UUID id, SchoolClassUpdateRequest request) {
    return dtoMapper.toSchoolClassDetailResponse(updateSchoolClass(id, request));
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
