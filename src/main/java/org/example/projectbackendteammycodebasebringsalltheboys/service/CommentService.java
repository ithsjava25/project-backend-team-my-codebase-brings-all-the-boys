package org.example.projectbackendteammycodebasebringsalltheboys.service;

import lombok.RequiredArgsConstructor;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Assignment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Comment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.CommentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final ActivityLogService activityLogService;

    @Transactional
    public Comment addComment(Assignment assignment, User author, String text) {
        Comment comment = new Comment();
        comment.setAssignment(assignment);
        comment.setAuthor(author);
        comment.setText(text);
        
        Comment saved = commentRepository.save(comment);
        
        activityLogService.log(author, "ADDED_COMMENT", "Comment", saved.getId(), 
                "Added comment to assignment: " + assignment.getTitle());
        
        return saved;
    }

    @Transactional(readOnly = true)
    public List<Comment> getCommentsByAssignment(Assignment assignment) {
        return commentRepository.findByAssignmentOrderByCreatedAtAsc(assignment);
    }
}
