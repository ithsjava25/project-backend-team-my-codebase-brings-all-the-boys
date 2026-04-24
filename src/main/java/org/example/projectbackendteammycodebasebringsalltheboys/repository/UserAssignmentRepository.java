package org.example.projectbackendteammycodebasebringsalltheboys.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Assignment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.UserAssignment;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.StudentAssignmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

  @Query(
      "SELECT COUNT(DISTINCT ua) FROM UserAssignment ua "
          + "JOIN ua.assignment a "
          + "JOIN a.course c "
          + "JOIN c.schoolClass sc "
          + "JOIN sc.enrollments e "
          + "WHERE e.user.id = :studentId AND ua.student.id = :studentId AND ua.status = :status")
  long countByStudentEnrollmentAndStatus(
      @Param("studentId") UUID studentId, @Param("status") StudentAssignmentStatus status);

  List<UserAssignment> findByStatusAndAssignment_Course_LeadTeacher_Id(
      StudentAssignmentStatus status, UUID teacherId);

  @Query(
      "SELECT DISTINCT ua FROM UserAssignment ua "
          + "JOIN ua.assignment a "
          + "JOIN a.course c "
          + "LEFT JOIN c.assistants ast "
          + "WHERE ua.status = :status AND (c.leadTeacher.id = :teacherId OR ast.id = :teacherId) "
          + "ORDER BY ua.updatedAt DESC")
  Page<UserAssignment> findByStatusAndTeacherConnection(
      @Param("status") StudentAssignmentStatus status,
      @Param("teacherId") UUID teacherId,
      Pageable pageable);

  @Query("SELECT ua FROM UserAssignment ua WHERE ua.status = :status ORDER BY ua.updatedAt DESC")
  Page<UserAssignment> findByStatus(
      @Param("status") StudentAssignmentStatus status, Pageable pageable);

  List<UserAssignment> findByStudentAndAssignmentIn(
      User student, Collection<Assignment> assignments);

  void deleteByStudent_Id(UUID studentId);
}
