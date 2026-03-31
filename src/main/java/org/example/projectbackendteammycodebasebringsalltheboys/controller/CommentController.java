package org.example.projectbackendteammycodebasebringsalltheboys.controller;

import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.comment.CommentRequest;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.comment.CommentResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Assignment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Comment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.mapper.DtoMapper;
import org.example.projectbackendteammycodebasebringsalltheboys.service.CaseService;
import org.example.projectbackendteammycodebasebringsalltheboys.service.CommentService;
import org.example.projectbackendteammycodebasebringsalltheboys.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

  private final CommentService commentService;
  private final CaseService caseService;
  private final UserService userService;
  private final DtoMapper dtoMapper;

  @PostMapping("/assignment/{assignmentId}")
  public ResponseEntity<CommentResponse> addComment(
      @PathVariable Long assignmentId,
      @Valid @RequestBody CommentRequest request,
      Principal principal) {

    User currentUser =
        userService
            .getUserByUsername(principal.getName())
            .orElseThrow(() -> new IllegalStateException("Current user not found"));

    Assignment assignment =
        caseService
            .getCaseById(assignmentId)
            .orElseThrow(() -> new IllegalArgumentException("Assignment not found"));

    Comment comment = commentService.addComment(assignment, currentUser, request.getText());

    return ResponseEntity.ok(dtoMapper.toCommentResponse(comment));
  }

  @GetMapping("/assignment/{assignmentId}")
  public ResponseEntity<List<CommentResponse>> getCommentsByAssignment(
      @PathVariable Long assignmentId) {

    Assignment assignment =
        caseService
            .getCaseById(assignmentId)
            .orElseThrow(() -> new IllegalArgumentException("Assignment not found"));

    List<Comment> comments = commentService.getCommentsByAssignment(assignment);
    List<CommentResponse> response =
        comments.stream().map(dtoMapper::toCommentResponse).collect(Collectors.toList());
    return ResponseEntity.ok(response);
  }
}
