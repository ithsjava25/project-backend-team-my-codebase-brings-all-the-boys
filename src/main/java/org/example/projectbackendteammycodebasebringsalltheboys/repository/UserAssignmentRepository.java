package org.example.projectbackendteammycodebasebringsalltheboys.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Assignment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.UserAssignment;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.StudentAssignmentStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAssignmentRepository extends JpaRepository<UserAssignment, UUID> {
  @EntityGraph(attributePaths = {"assignment", "student"})
  List<UserAssignment> findByStudent(User student);

  @EntityGraph(attributePaths = {"student"})
  List<UserAssignment> findByAssignment(Assignment assignment);

  Optional<UserAssignment> findByAssignmentAndStudent(Assignment assignment, User student);

  long countByStatus(StudentAssignmentStatus status);

  long countByStatusAndAssignment_Course_LeadTeacher_Id(
      StudentAssignmentStatus status, UUID teacherId);

  long countByStudent_IdAndStatus(UUID studentId, StudentAssignmentStatus status);

  @org.springframework.data.jpa.repository.Query(
      "SELECT COUNT(DISTINCT ua) FROM UserAssignment ua "
          + "JOIN ua.assignment a "
          + "JOIN a.course c "
          + "JOIN c.schoolClass sc "
          + "JOIN sc.enrollments e "
          + "WHERE e.user.id = :studentId AND ua.student.id = :studentId AND ua.status = :status")
  long countByStudentEnrollmentAndStatus(
      @org.springframework.data.repository.query.Param("studentId") UUID studentId,
      @org.springframework.data.repository.query.Param("status") StudentAssignmentStatus status);

  List<UserAssignment> findByStatusAndAssignment_Course_LeadTeacher_Id(
      StudentAssignmentStatus status, UUID teacherId);

  @org.springframework.data.jpa.repository.Query(
      "SELECT DISTINCT ua FROM UserAssignment ua "
          + "JOIN ua.assignment a "
          + "JOIN a.course c "
          + "LEFT JOIN c.assistants ast "
          + "WHERE ua.status = :status AND (c.leadTeacher.id = :teacherId OR ast.id = :teacherId)")
  List<UserAssignment> findByStatusAndTeacherConnection(
      @org.springframework.data.repository.query.Param("status") StudentAssignmentStatus status,
      @org.springframework.data.repository.query.Param("teacherId") UUID teacherId);

  List<UserAssignment> findByStatus(StudentAssignmentStatus status);

  List<UserAssignment> findByStudentAndAssignmentIn(
      User student, Collection<Assignment> assignments);

  void deleteByStudent_Id(UUID studentId);
}
