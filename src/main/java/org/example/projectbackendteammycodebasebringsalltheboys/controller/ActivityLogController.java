package org.example.projectbackendteammycodebasebringsalltheboys.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.user.ActivityLogResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.ActivityLog;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.exception.ForbiddenException;
import org.example.projectbackendteammycodebasebringsalltheboys.exception.NotFoundException;
import org.example.projectbackendteammycodebasebringsalltheboys.exception.UnauthorizedException;
import org.example.projectbackendteammycodebasebringsalltheboys.mapper.DtoMapper;
import org.example.projectbackendteammycodebasebringsalltheboys.service.ActivityLogService;
import org.example.projectbackendteammycodebasebringsalltheboys.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/activity-logs")
@RequiredArgsConstructor
public class ActivityLogController {

  private final ActivityLogService activityLogService;
  private final UserService userService;
  private final DtoMapper dtoMapper;

  @GetMapping("/user/{userId}")
  public ResponseEntity<List<ActivityLogResponse>> getUserActivityLogs(
      @PathVariable UUID userId, java.security.Principal principal) {

    if (principal == null) {
      throw new UnauthorizedException("Authentication is required");
    }

    User currentUser =
        userService
            .getUserByUsername(principal.getName())
            .orElseThrow(() -> new UnauthorizedException("Current user not found"));

    // Authorization check: Only the user themselves or an admin should see logs.
    if (!currentUser.getId().equals(userId)
        && !currentUser.getRole().getName().equals("ROLE_ADMIN")) {
      throw new ForbiddenException("You can only view your own activity logs.");
    }

    User targetUser =
        userService
            .getUserById(userId)
            .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

    List<ActivityLog> logs = activityLogService.getLogsForUser(targetUser);
    List<ActivityLogResponse> response =
        logs.stream().map(dtoMapper::toActivityLogResponse).collect(Collectors.toList());

    return ResponseEntity.ok(response);
  }

  @GetMapping("/entity/{entityType}/{entityId}")
  public ResponseEntity<List<ActivityLogResponse>> getEntityActivityLogs(
      @PathVariable String entityType,
      @PathVariable UUID entityId,
      java.security.Principal principal) {

    if (principal == null) {
      throw new UnauthorizedException("Authentication is required");
    }

    User currentUser =
        userService
            .getUserByUsername(principal.getName())
            .orElseThrow(() -> new UnauthorizedException("Current user not found"));

    // Authorization check: Only admin or teacher/creator can view entity logs.
    // This check needs to be more granular based on entity type and user role.
    // For now, we allow admins to see all entity logs.
    if (!currentUser.getRole().getName().equals("ROLE_ADMIN")) {
      throw new ForbiddenException("You are not authorized to view entity logs.");
    }

    List<ActivityLog> logs = activityLogService.getLogsForEntity(entityType, entityId);
    List<ActivityLogResponse> response =
        logs.stream().map(dtoMapper::toActivityLogResponse).collect(Collectors.toList());

    return ResponseEntity.ok(response);
  }
}
