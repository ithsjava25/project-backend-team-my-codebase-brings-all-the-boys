package org.example.projectbackendteammycodebasebringsalltheboys.service;

import lombok.RequiredArgsConstructor;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Assignment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Comment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.ClassEnrollmentRepository;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.UserAssignmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthorizationService {

  private final UserAssignmentRepository userAssignmentRepository;
  private final ClassEnrollmentRepository classEnrollmentRepository;

  @Transactional(readOnly = true)
  public boolean canAccessCase(User user, Assignment assignment) {
    if (isAdmin(user)) return true;
    if (isTeacher(user)
        && assignment.getCreator() != null
        && assignment.getCreator().getId().equals(user.getId())) {
      return true;
    }
    return isStudent(user)
        && userAssignmentRepository.findByAssignmentAndStudent(assignment, user).isPresent();
  }

  @Transactional(readOnly = true)
  public boolean canManageCase(User user, Assignment assignment) {
    return isAdmin(user)
        || (isTeacher(user)
            && assignment.getCreator() != null
            && assignment.getCreator().getId().equals(user.getId()));
  }

  @Transactional(readOnly = true)
  public boolean canCommentOnAssignment(User user, Assignment assignment) {
    if (isAdmin(user)) return true;
    if (assignment.getCreator() != null && assignment.getCreator().getId().equals(user.getId())) {
      return true;
    }
    if (assignment.getCourse() != null && assignment.getCourse().getSchoolClass() != null) {
      return classEnrollmentRepository
          .findByUserAndSchoolClass(user, assignment.getCourse().getSchoolClass())
          .isPresent();
    }
    return false;
  }

  @Transactional(readOnly = true)
  public boolean canViewAssignment(User user, Assignment assignment) {
    // Surface information logic
    if (isAdmin(user) || isTeacher(user)) return true;

    if (assignment.getCreator() != null && assignment.getCreator().getId().equals(user.getId())) {
      return true;
    }

    if (assignment.getCourse() != null && assignment.getCourse().getSchoolClass() != null) {
      return classEnrollmentRepository
          .findByUserAndSchoolClass(user, assignment.getCourse().getSchoolClass())
          .isPresent();
    }

    return false;
  }

  @Transactional(readOnly = true)
  public boolean canAccessAssignmentDetails(User user, Assignment assignment) {
    // For now, details access matches view access, but can be more restrictive (e.g. only for
    // published)
    return canViewAssignment(user, assignment);
  }

  @Transactional(readOnly = true)
  public boolean canModifyAssignment(User user, Assignment assignment) {
    return user.getRole().getName().equals("ROLE_ADMIN")
        || user.getRole().getName().equals("ROLE_TEACHER")
        || assignment.getCreator().getId().equals(user.getId());
  }

  @Transactional(readOnly = true)
  public boolean canModifyComment(User user, Comment comment) {
    return user.getRole().getName().equals("ROLE_ADMIN")
        || user.getRole().getName().equals("ROLE_TEACHER")
        || comment.getAuthor().getId().equals(user.getId());
  }

  private boolean isAdmin(User user) {
    return user.getRole().getName().equalsIgnoreCase("ROLE_ADMIN");
  }

  private boolean isTeacher(User user) {
    return user.getRole().getName().equalsIgnoreCase("ROLE_TEACHER");
  }

  private boolean isStudent(User user) {
    return user.getRole().getName().equalsIgnoreCase("ROLE_STUDENT");
  }
}
