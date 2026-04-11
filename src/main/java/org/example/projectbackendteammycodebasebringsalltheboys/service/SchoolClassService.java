package org.example.projectbackendteammycodebasebringsalltheboys.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.projectbackendteammycodebasebringsalltheboys.annotation.LogActivity;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.SchoolClass;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.ActivityAction;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.EntityType;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.SchoolClassRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SchoolClassService {

  private final SchoolClassRepository schoolClassRepository;

  @LogActivity(action = ActivityAction.CREATED, entityType = EntityType.SCHOOL_CLASS, orphan = true)
  @Transactional
  public SchoolClass createClass(String name, String description, User creator) {
    if (schoolClassRepository.findByName(name).isPresent()) {
      throw new IllegalArgumentException("Class with name " + name + " already exists");
    }

    SchoolClass schoolClass = new SchoolClass();
    schoolClass.setName(name);
    schoolClass.setDescription(description);

    return schoolClassRepository.save(schoolClass);
  }

  @Transactional(readOnly = true)
  public List<SchoolClass> getAllClasses() {
    return schoolClassRepository.findAll();
  }

  @Transactional(readOnly = true)
  public Optional<SchoolClass> getClassById(UUID id) {
    return schoolClassRepository.findById(id);
  }

  @Transactional(readOnly = true)
  public Optional<SchoolClass> getClassByName(String name) {
    return schoolClassRepository.findByName(name);
  }
}
