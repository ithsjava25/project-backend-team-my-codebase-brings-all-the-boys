package org.example.projectbackendteammycodebasebringsalltheboys.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Assignment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Comment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Course;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.SchoolClass;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.UserAssignment;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.ClassRole;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.ClassEnrollmentRepository;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.CourseRepository;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.UserAssignmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthorizationService {

  private final UserAssignmentRepository userAssignmentRepository;
  private final ClassEnrollmentRepository classEnrollmentRepository;
  private final CourseRepository courseRepository;

  @Transactional(readOnly = true)
  public boolean isMemberOfClass(User user, SchoolClass schoolClass) {
    if (isAdmin(user)) return true;
    return classEnrollmentRepository.existsByUserAndSchoolClass(user, schoolClass);
  }

  @Transactional(readOnly = true)
  public boolean isTeacherOrMentorInClass(User user, SchoolClass schoolClass) {
    if (isAdmin(user)) return true;
    return classEnrollmentRepository.existsByUserAndSchoolClassAndClassRoleIn(
        user, schoolClass, List.of(ClassRole.TEACHER, ClassRole.MENTOR));
  }

  @Transactional(readOnly = true)
  public boolean canCreateCourseInClass(User user, SchoolClass schoolClass) {
    if (isAdmin(user)) return true;
    // Require a TEACHER or MENTOR enrollment in the class to create a course
    return isTeacher(user) && isTeacherOrMentorInClass(user, schoolClass);
  }

  @Transactional(readOnly = true)
  public boolean canModifyCourse(User user, Course course) {
    if (isAdmin(user)) return true;
    if (!isTeacher(user)) return false;

    boolean isLead =
        course.getLeadTeacher() != null && course.getLeadTeacher().getId().equals(user.getId());
    // Assistants might be allowed to modify some aspects, but usually lead or admin
    return isLead;
  }

  @Transactional(readOnly = true)
  public boolean canViewUserProfile(User actor, User target) {
    if (isAdmin(actor)) return true;
    if (actor.getId().equals(target.getId())) return true;

    // Check if they share any classes or courses
    // Short-circuit: check shared classes (usually fewer) before courses
    return classEnrollmentRepository.hasSharedSchoolClass(actor.getId(), target.getId())
        || courseRepository.hasSharedCourse(actor.getId(), target.getId());
  }

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
      return classEnrollmentRepository.existsByUserAndSchoolClass(
          user, assignment.getCourse().getSchoolClass());
    }
    return false;
  }

  @Transactional(readOnly = true)
  public boolean canViewAssignment(User user, Assignment assignment) {
    // Surface information logic
    if (isAdmin(user)) return true;

    if (assignment.getCreator() != null && assignment.getCreator().getId().equals(user.getId())) {
      return true;
    }

    if (isTeacher(user)) {
      if (assignment.getCourse() == null)
        return false; // Orphan assignments: only creator or admin (checked above)

      boolean isLead =
          assignment.getCourse().getLeadTeacher() != null
              && assignment.getCourse().getLeadTeacher().getId().equals(user.getId());
      boolean isAssistant =
          assignment.getCourse().getAssistants().stream()
              .anyMatch(a -> a.getId().equals(user.getId()));

      return isLead || isAssistant;
    }

    if (assignment.getCourse() != null && assignment.getCourse().getSchoolClass() != null) {
      return classEnrollmentRepository.existsByUserAndSchoolClass(
          user, assignment.getCourse().getSchoolClass());
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
    if (isAdmin(user)) return true;

    if (assignment.getCreator() != null && assignment.getCreator().getId().equals(user.getId())) {
      return true;
    }

    if (isTeacher(user) && assignment.getCourse() != null) {
      boolean isLead =
          assignment.getCourse().getLeadTeacher() != null
              && assignment.getCourse().getLeadTeacher().getId().equals(user.getId());
      return isLead;
    }

    return false;
  }

  @Transactional(readOnly = true)
  public boolean canModifyComment(User user, Comment comment) {
    return user.getRole().getName().equals("ROLE_ADMIN")
        || user.getRole().getName().equals("ROLE_TEACHER")
        || comment.getAuthor().getId().equals(user.getId());
  }

  @Transactional(readOnly = true)
  public boolean canAccessUserAssignment(User user, UserAssignment ua) {
    if (isAdmin(user)) return true;

    if (isStudent(user)) {
      return ua.getStudent() != null && ua.getStudent().getId().equals(user.getId());
    }

    if (isTeacher(user) && ua.getAssignment() != null) {
      return canViewAssignment(user, ua.getAssignment());
    }

    return false;
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
