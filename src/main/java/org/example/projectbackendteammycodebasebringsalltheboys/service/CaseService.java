package org.example.projectbackendteammycodebasebringsalltheboys.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Assignment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Course;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.AssignmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CaseService {

  private final AssignmentRepository assignmentRepository;
  private final ActivityLogService activityLogService;

  @Transactional
  public Assignment createCase(String title, String description, User creator) {
    return createCase(title, description, creator, null);
  }

  @Transactional
  public Assignment createCase(String title, String description, User creator, Course course) {
    Assignment assignment = new Assignment();
    assignment.setTitle(title);
    assignment.setDescription(description);
    assignment.setCreator(creator);
    assignment.setCourse(course);

    Assignment saved = assignmentRepository.save(assignment);

    String logMessage = "Case created: " + title;
    if (course != null) {
      logMessage += " for course: " + course.getName();
    }

    activityLogService.log(creator, "CREATED_CASE", "Assignment", saved.getId(), logMessage);

    return saved;
  }

  @Transactional(readOnly = true)
  public List<Assignment> getAllCases() {
    return assignmentRepository.findAll();
  }

  @Transactional(readOnly = true)
  public Optional<Assignment> getCaseById(Long id) {
    return assignmentRepository.findById(id);
  }

  @Transactional(readOnly = true)
  public List<Assignment> getCasesByCreator(User creator) {
    return assignmentRepository.findByCreator(creator);
  }
}
