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

  @Transactional
  @LogActivity(
      action = ActivityAction.CREATED,
      entityType = EntityType.ASSIGNMENT,
      orphan = true,
      actorParamIndex = 2)
  public Assignment createCase(String title, String description, User creator) {
    return createCase(title, description, creator, null, null);
  }

  @Transactional
  @LogActivity(
      action = ActivityAction.CREATED,
      entityType = EntityType.ASSIGNMENT,
      orphan = true,
      actorParamIndex = 2)
  public Assignment createCase(String title, String description, User creator, Course course) {
    return createCase(title, description, creator, course, null);
  }

  @LogActivity(action = ActivityAction.CREATED, entityType = EntityType.ASSIGNMENT, orphan = true)
  @Transactional
  public Assignment createCase(
      String title, String description, User creator, java.time.LocalDateTime deadline) {
    return createCase(title, description, creator, null, deadline);
  }

  @LogActivity(action = ActivityAction.CREATED, entityType = EntityType.ASSIGNMENT, orphan = true)
  @Transactional
  public Assignment createCase(
      String title,
      String description,
      User creator,
      Course course,
      java.time.LocalDateTime deadline) {
    if (course != null
        && course.getEndDate() != null
        && deadline != null
        && deadline.isAfter(course.getEndDate())) {
      throw new org.example.projectbackendteammycodebasebringsalltheboys.exception
          .BadRequestException("Assignment deadline cannot be after course end date.");
    }
    Assignment assignment = new Assignment();
    assignment.setTitle(title);
    assignment.setDescription(description);
    assignment.setCreator(creator);
    assignment.setCourse(course);
    assignment.setDeadline(deadline);

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
