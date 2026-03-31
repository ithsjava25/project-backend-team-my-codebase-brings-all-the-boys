package org.example.projectbackendteammycodebasebringsalltheboys.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.SchoolClass;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.SchoolClassRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SchoolClassService {

  private final SchoolClassRepository schoolClassRepository;
  private final ActivityLogService activityLogService;

  @Transactional
  public SchoolClass createClass(
      String name,
      String description,
      org.example.projectbackendteammycodebasebringsalltheboys.entity.User creator) {
    if (schoolClassRepository.findByName(name).isPresent()) {
      throw new IllegalArgumentException("Class with name " + name + " already exists");
    }

    SchoolClass schoolClass = new SchoolClass();
    schoolClass.setName(name);
    schoolClass.setDescription(description);

    SchoolClass saved = schoolClassRepository.save(schoolClass);

    activityLogService.log(
        creator, "CREATED_CLASS", "SchoolClass", saved.getId(), "Class created: " + name);

    return saved;
  }

  @Transactional(readOnly = true)
  public List<SchoolClass> getAllClasses() {
    return schoolClassRepository.findAll();
  }

  @Transactional(readOnly = true)
  public Optional<SchoolClass> getClassById(Long id) {
    return schoolClassRepository.findById(id);
  }

  @Transactional(readOnly = true)
  public Optional<SchoolClass> getClassByName(String name) {
    return schoolClassRepository.findByName(name);
  }
}
