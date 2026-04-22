package org.example.projectbackendteammycodebasebringsalltheboys.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Assignment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, UUID> {
  List<Assignment> findByCreator(User creator);

  Page<Assignment> findByCreator_Id(UUID creatorId, Pageable pageable);

  List<Assignment> findByDeadlineBetween(LocalDateTime start, LocalDateTime end);

  @Modifying
  @Query("UPDATE Assignment a SET a.creator = null WHERE a.creator.id = :userId")
  void nullifyCreator(@Param("userId") UUID userId);

  List<Assignment> findByCourse_LeadTeacher_IdAndDeadlineBetween(
      UUID teacherId, LocalDateTime start, LocalDateTime end);

  Page<Assignment> findByCourse_LeadTeacher_Id(UUID teacherId, Pageable pageable);

  boolean existsByCourse_IdAndDeadlineAfter(UUID courseId, LocalDateTime deadline);

  @Query(
      "SELECT DISTINCT a FROM Assignment a "
          + "LEFT JOIN a.course c "
          + "LEFT JOIN c.assistants asst "
          + "WHERE a.creator.id = :teacherId OR c.leadTeacher.id = :teacherId OR asst.id = :teacherId")
  Page<Assignment> findAccessibleByTeacher(UUID teacherId, Pageable pageable);

  @Query(
      "SELECT DISTINCT a FROM Assignment a JOIN a.course c JOIN c.schoolClass sc JOIN sc.enrollments e WHERE e.user.id = :studentId")
  Page<Assignment> findByStudentEnrollment(UUID studentId, Pageable pageable);

  @Query(
      "SELECT DISTINCT a FROM Assignment a JOIN a.course c JOIN c.schoolClass sc JOIN sc.enrollments e WHERE e.user.id = :studentId AND a.deadline BETWEEN :start AND :end")
  List<Assignment> findByCourse_SchoolClass_Enrollments_User_IdAndDeadlineBetween(
      UUID studentId, java.time.LocalDateTime start, java.time.LocalDateTime end);
}
