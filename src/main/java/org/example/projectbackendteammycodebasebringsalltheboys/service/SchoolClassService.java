package org.example.projectbackendteammycodebasebringsalltheboys.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.schoolclass.SchoolClassDetailResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.schoolclass.SchoolClassSurfaceResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.SchoolClass;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.ClassRole;
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
    boolean isTeacherOrAdmin =
        currentUser.getRole().getName().equals("ROLE_ADMIN")
            || currentUser.getRole().getName().equals("ROLE_TEACHER");

    boolean isMentor =
        schoolClass.getEnrollments().stream()
            .anyMatch(
                e ->
                    e.getUser() != null
                        && e.getUser().getId().equals(currentUser.getId())
                        && e.getClassRole() == ClassRole.MENTOR);

    boolean isEnrolledStudent =
        schoolClass.getEnrollments().stream()
            .anyMatch(
                e ->
                    e.getUser() != null
                        && e.getUser().getId().equals(currentUser.getId())
                        && e.getClassRole() == ClassRole.STUDENT);

    if (isTeacherOrAdmin || isMentor || isEnrolledStudent) {
      return dtoMapper.toSchoolClassDetailResponse(schoolClass);
    } else {
      throw new org.example.projectbackendteammycodebasebringsalltheboys.exception
          .ForbiddenException("You do not have permission to view this school class's details.");
    }
  }

  @Transactional(readOnly = true)
  public Optional<SchoolClassDetailResponse> getSchoolClassDetailDto(UUID id) {
    return schoolClassRepository.findById(id).map(dtoMapper::toSchoolClassDetailResponse);
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

  @Transactional
  @org.example.projectbackendteammycodebasebringsalltheboys.annotation.LogActivity(
      action =
          org.example.projectbackendteammycodebasebringsalltheboys.enums.ActivityAction.CREATED,
      entityType =
          org.example.projectbackendteammycodebasebringsalltheboys.enums.EntityType.SCHOOL_CLASS)
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
    if (!schoolClassRepository.existsById(id)) {
      throw new NotFoundException("School class not found with id: " + id);
    }
    schoolClassRepository.deleteById(id);
  }
}
