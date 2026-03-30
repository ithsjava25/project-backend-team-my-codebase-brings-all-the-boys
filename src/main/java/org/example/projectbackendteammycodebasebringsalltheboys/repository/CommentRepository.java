package org.example.projectbackendteammycodebasebringsalltheboys.repository;

import java.util.List;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Assignment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
  List<Comment> findByAssignmentOrderByCreatedAtAsc(Assignment assignment);
}
