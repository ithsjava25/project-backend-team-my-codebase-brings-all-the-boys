package org.example.projectbackendteammycodebasebringsalltheboys.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.SchoolClass;
import org.example.projectbackendteammycodebasebringsalltheboys.exception.NotFoundException;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.SchoolClassRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SchoolClassService {

  private final SchoolClassRepository schoolClassRepository;

  @Transactional(readOnly = true)
  public Optional<SchoolClass> getSchoolClassById(UUID id) {
    return schoolClassRepository.findById(id);
  }

  @Transactional(readOnly = true)
  public List<SchoolClass> getAllSchoolClasses() {
    return schoolClassRepository.findAll();
  }

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
  public void deleteSchoolClass(UUID id) {
    if (!schoolClassRepository.existsById(id)) {
      throw new NotFoundException("School class not found with id: " + id);
    }
    schoolClassRepository.deleteById(id);
  }
}
