package org.example.projectbackendteammycodebasebringsalltheboys.controller;

import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.comment.CommentRequest;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.comment.CommentResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Assignment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Comment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.UserAssignment;
import org.example.projectbackendteammycodebasebringsalltheboys.exception.ForbiddenException;
import org.example.projectbackendteammycodebasebringsalltheboys.exception.NotFoundException;
import org.example.projectbackendteammycodebasebringsalltheboys.exception.UnauthorizedException;
import org.example.projectbackendteammycodebasebringsalltheboys.mapper.DtoMapper;
import org.example.projectbackendteammycodebasebringsalltheboys.service.AuthorizationService;
import org.example.projectbackendteammycodebasebringsalltheboys.service.CaseService;
import org.example.projectbackendteammycodebasebringsalltheboys.service.CommentService;
import org.example.projectbackendteammycodebasebringsalltheboys.service.UserAssignmentService;
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
  private final AuthorizationService authorizationService;
  private final DtoMapper dtoMapper;
  private final UserAssignmentService userAssignmentService;

  @PostMapping("/assignment/{assignmentId}")
  public ResponseEntity<CommentResponse> addComment(
      @PathVariable UUID assignmentId,
      @Valid @RequestBody CommentRequest request,
      Principal principal) {

    if (principal == null) {
      throw new UnauthorizedException("Authentication is required");
    }

    User currentUser =
        userService
            .getUserByUsername(principal.getName())
            .orElseThrow(() -> new UnauthorizedException("Current user not found"));

    Assignment assignment =
        caseService
            .getCaseById(assignmentId)
            .orElseThrow(
                () -> new NotFoundException("Assignment not found with id: " + assignmentId));

    if (!authorizationService.canCommentOnAssignment(currentUser, assignment)) {
      throw new ForbiddenException("You are not authorized to comment on this assignment");
    }
    Comment comment = commentService.addComment(assignment, currentUser, request.getText());

    return ResponseEntity.ok(dtoMapper.toCommentResponse(comment));
  }

  @GetMapping("/assignment/{assignmentId}")
  public ResponseEntity<List<CommentResponse>> getCommentsByAssignment(
      @PathVariable UUID assignmentId, Principal principal) {

    if (principal == null) {
      throw new UnauthorizedException("Authentication is required");
    }

    Assignment assignment =
        caseService
            .getCaseById(assignmentId)
            .orElseThrow(
                () -> new NotFoundException("Assignment not found with id: " + assignmentId));

    User currentUser =
        userService
            .getUserByUsername(principal.getName())
            .orElseThrow(() -> new UnauthorizedException("Current user not found"));

    if (!authorizationService.canViewAssignment(currentUser, assignment)) {
      throw new ForbiddenException("You are not authorized to view comments for this assignment");
    }

    List<Comment> comments = commentService.getCommentsByAssignment(assignment);
    List<CommentResponse> response =
        comments.stream().map(dtoMapper::toCommentResponse).collect(Collectors.toList());
    return ResponseEntity.ok(response);
  }

  @PostMapping("/user-assignment/{userAssignmentId}")
  public ResponseEntity<CommentResponse> addCommentToUserAssignment(
      @PathVariable UUID userAssignmentId,
      @Valid @RequestBody CommentRequest request,
      Principal principal) {

    if (principal == null) {
      throw new UnauthorizedException("Authentication is required");
    }

    User currentUser =
        userService
            .getUserByUsername(principal.getName())
            .orElseThrow(() -> new UnauthorizedException("Current user not found"));

    UserAssignment ua =
        userAssignmentService
            .getById(userAssignmentId)
            .orElseThrow(
                () ->
                    new NotFoundException("UserAssignment not found with id: " + userAssignmentId));

    if (!authorizationService.canAccessUserAssignment(currentUser, ua)) {
      throw new ForbiddenException("You are not authorized to comment on this user assignment");
    }

    Comment comment = commentService.addCommentToUserAssignment(ua, currentUser, request.getText());

    return ResponseEntity.ok(dtoMapper.toCommentResponse(comment));
  }

  @GetMapping("/user-assignment/{userAssignmentId}")
  public ResponseEntity<List<CommentResponse>> getCommentsByUserAssignment(
      @PathVariable UUID userAssignmentId, Principal principal) {

    if (principal == null) {
      throw new UnauthorizedException("Authentication is required");
    }

    UserAssignment ua =
        userAssignmentService
            .getById(userAssignmentId)
            .orElseThrow(
                () ->
                    new NotFoundException("UserAssignment not found with id: " + userAssignmentId));

    User currentUser =
        userService
            .getUserByUsername(principal.getName())
            .orElseThrow(() -> new UnauthorizedException("Current user not found"));

    if (!authorizationService.canAccessUserAssignment(currentUser, ua)) {
      throw new ForbiddenException(
          "You are not authorized to view comments for this user assignment");
    }

    List<Comment> comments = commentService.getCommentsByUserAssignment(ua);
    List<CommentResponse> response =
        comments.stream().map(dtoMapper::toCommentResponse).collect(Collectors.toList());
    return ResponseEntity.ok(response);
  }
}
