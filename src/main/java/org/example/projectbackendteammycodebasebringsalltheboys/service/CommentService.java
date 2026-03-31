package org.example.projectbackendteammycodebasebringsalltheboys.service;

import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Assignment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Comment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.ActivityAction;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.EntityType;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.CommentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

  private final CommentRepository commentRepository;
  private final ActivityLogService activityLogService;

  @Transactional
  public Comment addComment(Assignment assignment, User author, String text) {
    Objects.requireNonNull(assignment, "Assignment cannot be null");
    Objects.requireNonNull(author, "Author cannot be null");
    if (text == null || text.isBlank()) {
      throw new IllegalArgumentException("Comment text cannot be empty");
    }
    Comment comment = new Comment();
    comment.setAssignment(assignment);
    comment.setAuthor(author);
    comment.setText(text);

    Comment saved = commentRepository.save(comment);

    activityLogService.log(
        author,
        assignment.getId(),
        ActivityAction.ADDED,
        EntityType.COMMENT,
        saved.getId(),
        "Added comment to assignment: " + assignment.getTitle());

    return saved;
  }

  @Transactional(readOnly = true)
  public List<Comment> getCommentsByAssignment(Assignment assignment) {
    return commentRepository.findByAssignmentOrderByCreatedAtAsc(assignment);
  }
}
