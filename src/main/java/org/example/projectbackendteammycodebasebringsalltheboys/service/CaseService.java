package org.example.projectbackendteammycodebasebringsalltheboys.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.projectbackendteammycodebasebringsalltheboys.annotation.LogActivity;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Assignment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Course;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.ActivityAction;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.EntityType;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.AssignmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CaseService {

  private final AssignmentRepository assignmentRepository;
  private final ActivityLogService activityLogService;

  @LogActivity(action = ActivityAction.CREATED, entity = EntityType.ASSIGNMENT, noCase = true)
  @Transactional
  public Assignment createCase(String title, String description, User creator) {
    return createCase(title, description, creator, null);
  }

  @LogActivity(action = ActivityAction.CREATED, entity = EntityType.ASSIGNMENT, course = course, noCase = true)
  @Transactional
  public Assignment createCase(String title, String description, User creator, Course course) {
    Assignment assignment = new Assignment();
    assignment.setTitle(title);
    assignment.setDescription(description);
    assignment.setCreator(creator);
    assignment.setCourse(course);

    return assignmentRepository.save(assignment);
  }

  @Transactional(readOnly = true)
  public List<Assignment> getAllCases() {
    return assignmentRepository.findAll();
  }

  @Transactional(readOnly = true)
  public Optional<Assignment> getCaseById(UUID id) {
    return assignmentRepository.findById(id);
  }

  @Transactional(readOnly = true)
  public List<Assignment> getCasesByCreator(User creator) {
    return assignmentRepository.findByCreator(creator);
  }
}
