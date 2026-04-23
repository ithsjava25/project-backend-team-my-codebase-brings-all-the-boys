package org.example.projectbackendteammycodebasebringsalltheboys.repository;

import java.util.List;
import java.util.UUID;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Assignment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Comment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.UserAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {
  List<Comment> findByAssignment(Assignment assignment);

  List<Comment> findByAssignmentOrderByCreatedAtAsc(Assignment assignment);

  void deleteByAuthor_Id(UUID authorId);

  List<Comment> findByUserAssignment(UserAssignment userAssignment);

  List<Comment> findByUserAssignmentOrderByCreatedAtAsc(UserAssignment userAssignment);
}
