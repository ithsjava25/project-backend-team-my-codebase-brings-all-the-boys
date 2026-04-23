package org.example.projectbackendteammycodebasebringsalltheboys.repository;

import java.util.List;
import java.util.UUID;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Assignment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Comment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.UserAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {
  List<Comment> findByAssignment(Assignment assignment);

  List<Comment> findByAssignmentOrderByCreatedAtAsc(Assignment assignment);

  @Modifying
  @Transactional
  void deleteByAssignment(Assignment assignment);

  @Modifying
  @Transactional
  void deleteByAuthor_Id(UUID authorId);

  List<Comment> findByUserAssignment(UserAssignment userAssignment);

  List<Comment> findByUserAssignmentOrderByCreatedAtAsc(UserAssignment userAssignment);

  @Modifying
  @Transactional
  void deleteByUserAssignment_Student_Id(UUID studentId);
}
