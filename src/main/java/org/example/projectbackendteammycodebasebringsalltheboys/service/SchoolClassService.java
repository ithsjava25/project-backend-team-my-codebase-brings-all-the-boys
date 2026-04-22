package org.example.projectbackendteammycodebasebringsalltheboys.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.example.projectbackendteammycodebasebringsalltheboys.annotation.LogActivity;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.schoolclass.SchoolClassDetailResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.schoolclass.SchoolClassSurfaceResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.SchoolClass;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.ActivityAction;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.ClassRole;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.EntityType;
import org.example.projectbackendteammycodebasebringsalltheboys.exception.NotFoundException;
import org.example.projectbackendteammycodebasebringsalltheboys.mapper.DtoMapper;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.SchoolClassRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SchoolClassService {

  private final SchoolClassRepository schoolClassRepository;
  private final DtoMapper dtoMapper;

  @Transactional(readOnly = true)
  public SchoolClassDetailResponse getSchoolClassDetailDto(
      UUID id, org.example.projectbackendteammycodebasebringsalltheboys.entity.User currentUser) {
    SchoolClass schoolClass =
        schoolClassRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("School class not found with id: " + id));

    // Authorization check: Allow Admins, Teachers, Mentors, and enrolled Students to view details
    String roleName =
        (currentUser != null && currentUser.getRole() != null)
            ? currentUser.getRole().getName()
            : "";
    boolean isTeacherOrAdmin = roleName.equals("ROLE_ADMIN") || roleName.equals("ROLE_TEACHER");

    boolean isMentor = false;
    boolean isEnrolledStudent = false;

    if (currentUser != null) {
      for (org.example.projectbackendteammycodebasebringsalltheboys.entity.ClassEnrollment
          enrollment : schoolClass.getEnrollments()) {
        if (enrollment.getUser() != null
            && enrollment.getUser().getId().equals(currentUser.getId())) {
          if (enrollment.getClassRole() == ClassRole.MENTOR) {
            isMentor = true;
          } else if (enrollment.getClassRole() == ClassRole.STUDENT) {
            isEnrolledStudent = true;
          }
        }
        if (isMentor && isEnrolledStudent) break;
      }
    }

    if (isTeacherOrAdmin || isMentor || isEnrolledStudent) {
      return dtoMapper.toSchoolClassDetailResponse(schoolClass);
    } else {
      throw new org.example.projectbackendteammycodebasebringsalltheboys.exception
          .ForbiddenException("You do not have permission to view this school class's details.");
    }
  }

  @Transactional(readOnly = true)
  public List<SchoolClassSurfaceResponse> getAllSchoolClassesDto() {
    return schoolClassRepository.findAll().stream()
        .map(dtoMapper::toSchoolClassSurfaceResponse)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public Optional<SchoolClass> getSchoolClassById(UUID id) {
    return schoolClassRepository.findById(id);
  }

  @Transactional(readOnly = true)
  public List<SchoolClass> getAllSchoolClasses() {
    return schoolClassRepository.findAll();
  }

  @LogActivity(action = ActivityAction.CREATED, entityType = EntityType.SCHOOL_CLASS, orphan = true)
  @Transactional
  public SchoolClass createSchoolClass(String name, String description) {
    if (schoolClassRepository.findByName(name).isPresent()) {
      throw new IllegalStateException("School class with name '" + name + "' already exists.");
    }
    SchoolClass schoolClass = new SchoolClass();
    schoolClass.setName(name);
    schoolClass.setDescription(description);
    return schoolClassRepository.save(schoolClass);
  }

  @Transactional
  @org.example.projectbackendteammycodebasebringsalltheboys.annotation.LogActivity(
      action =
          org.example.projectbackendteammycodebasebringsalltheboys.enums.ActivityAction.UPDATED,
      entityType =
          org.example.projectbackendteammycodebasebringsalltheboys.enums.EntityType.SCHOOL_CLASS,
      parentIdParamIndex = 0)
  public SchoolClass updateSchoolClass(UUID id, String name, String description) {
    SchoolClass schoolClass =
        schoolClassRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("School class not found with id: " + id));

    if (!schoolClass.getName().equals(name) && schoolClassRepository.findByName(name).isPresent()) {
      throw new IllegalStateException("School class with name '" + name + "' already exists.");
    }

    schoolClass.setName(name);
    schoolClass.setDescription(description);
    return schoolClassRepository.save(schoolClass);
  }

  @Transactional
  @org.example.projectbackendteammycodebasebringsalltheboys.annotation.LogActivity(
      action =
          org.example.projectbackendteammycodebasebringsalltheboys.enums.ActivityAction.DELETED,
      entityType =
          org.example.projectbackendteammycodebasebringsalltheboys.enums.EntityType.SCHOOL_CLASS,
      parentIdParamIndex = 0)
  public void deleteSchoolClass(UUID id) {
    SchoolClass schoolClass =
        schoolClassRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("School class not found with id: " + id));

    // Ensure all related entities are also soft-deleted by clearing the collections
    // and letting orphanRemoval = true take care of it.
    schoolClass.getCourses().clear();
    schoolClass.getEnrollments().clear();

    schoolClassRepository.delete(schoolClass);
  }
}
