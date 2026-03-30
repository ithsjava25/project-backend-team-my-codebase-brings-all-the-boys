package org.example.projectbackendteammycodebasebringsalltheboys.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Assignment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.UserAssignment;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.StudentAssignmentStatus;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.UserAssignmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserAssignmentService {

  private final UserAssignmentRepository userAssignmentRepository;
  private final ActivityLogService activityLogService;

  @Transactional
  public UserAssignment assignToStudent(Assignment assignment, User student, User assigner) {
    UserAssignment ua = new UserAssignment();
    ua.setAssignment(assignment);
    ua.setStudent(student);
    ua.setStatus(StudentAssignmentStatus.ASSIGNED);

    UserAssignment saved = userAssignmentRepository.save(ua);

    activityLogService.log(
        assigner,
        "ASSIGNED_CASE",
        "UserAssignment",
        saved.getId(),
        "Assigned case: " + assignment.getTitle() + " to student: " + student.getUsername());

    return saved;
  }

  @Transactional
  public void submitAssignment(UserAssignment ua) {
    if (ua.getStatus() != StudentAssignmentStatus.ASSIGNED) {
      throw new IllegalStateException("Cannot submit assignment in status: " + ua.getStatus());
    }
    ua.setStatus(StudentAssignmentStatus.TURNED_IN);
    ua.setTurnedInAt(LocalDateTime.now());
    userAssignmentRepository.save(ua);

    activityLogService.log(
        ua.getStudent(),
        "SUBMITTED_ASSIGNMENT",
        "UserAssignment",
        ua.getId(),
        "Student turned in assignment: " + ua.getAssignment().getTitle());
  }

  @Transactional
  public void evaluateAssignment(UserAssignment ua, String grade, String feedback, User evaluator) {
    if (ua.getStatus() != StudentAssignmentStatus.TURNED_IN) {
      throw new IllegalStateException("Cannot evaluate assignment in status: " + ua.getStatus());
    }
    ua.setStatus(StudentAssignmentStatus.EVALUATED);
    ua.setGrade(grade);
    ua.setFeedback(feedback);
    userAssignmentRepository.save(ua);

    activityLogService.log(
        evaluator,
        "EVALUATED_ASSIGNMENT",
        "UserAssignment",
        ua.getId(),
        "Teacher evaluated assignment with grade: " + grade);
  }

  @Transactional(readOnly = true)
  public List<UserAssignment> getAssignmentsForStudent(User student) {
    return userAssignmentRepository.findByStudent(student);
  }

  @Transactional(readOnly = true)
  public Optional<UserAssignment> getByAssignmentAndStudent(Assignment assignment, User student) {
    return userAssignmentRepository.findByAssignmentAndStudent(assignment, student);
  }
}
