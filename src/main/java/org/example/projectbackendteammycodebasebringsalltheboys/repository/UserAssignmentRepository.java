package org.example.projectbackendteammycodebasebringsalltheboys.repository;

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

  Optional<UserAssignment> findByAssignmentAndStudent(Assignment assignment, User student);

  long countByStatus(StudentAssignmentStatus status);

  long countByStatusAndAssignment_Course_LeadTeacher_Id(
      StudentAssignmentStatus status, UUID teacherId);

  long countByStudent_IdAndStatus(UUID studentId, StudentAssignmentStatus status);

  List<UserAssignment> findByStatusAndAssignment_Course_LeadTeacher_Id(
      StudentAssignmentStatus status, UUID teacherId);
}
