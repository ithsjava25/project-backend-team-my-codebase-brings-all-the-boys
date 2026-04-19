package org.example.projectbackendteammycodebasebringsalltheboys.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Assignment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, UUID> {
  List<Assignment> findByCreator(User creator);

  List<Assignment> findByDeadlineBetween(LocalDateTime start, LocalDateTime end);

  List<Assignment> findByCourse_LeadTeacher_IdAndDeadlineBetween(
      UUID teacherId, LocalDateTime start, LocalDateTime end);

  List<Assignment> findByCourse_SchoolClass_Enrollments_User_IdAndDeadlineBetween(
      UUID studentId, LocalDateTime start, LocalDateTime end);
}
