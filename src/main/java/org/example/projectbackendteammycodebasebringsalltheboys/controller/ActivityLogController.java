package org.example.projectbackendteammycodebasebringsalltheboys.controller;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.user.ActivityLogResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.ActivityLog;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.EntityType;
import org.example.projectbackendteammycodebasebringsalltheboys.exception.ForbiddenException;
import org.example.projectbackendteammycodebasebringsalltheboys.exception.NotFoundException;
import org.example.projectbackendteammycodebasebringsalltheboys.exception.UnauthorizedException;
import org.example.projectbackendteammycodebasebringsalltheboys.mapper.DtoMapper;
import org.example.projectbackendteammycodebasebringsalltheboys.service.ActivityLogService;
import org.example.projectbackendteammycodebasebringsalltheboys.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
  public ResponseEntity<Page<ActivityLogResponse>> getUserActivityLogs(
      @PathVariable UUID userId, java.security.Principal principal, Pageable pageable) {

    if (principal == null) {
      throw new UnauthorizedException("Authentication is required");
    }

    User currentUser =
        userService
            .getUserByUsername(principal.getName())
            .orElseThrow(() -> new UnauthorizedException("Current user not found"));

    if (!currentUser.getId().equals(userId)
        && !currentUser.getRole().getName().equals("ROLE_ADMIN")) {
      throw new ForbiddenException("You can only view your own activity logs.");
    }

    User targetUser =
        userService
            .getUserById(userId)
            .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

    Page<ActivityLog> logs = activityLogService.getLogsForUser(targetUser, pageable);
    Page<ActivityLogResponse> response = logs.map(dtoMapper::toActivityLogResponse);

    return ResponseEntity.ok(response);
  }

  @GetMapping("/entity/{entityType}/{entityId}")
  public ResponseEntity<Page<ActivityLogResponse>> getEntityActivityLogs(
      @PathVariable EntityType entityType,
      @PathVariable UUID entityId,
      java.security.Principal principal,
      Pageable pageable) {

    if (principal == null) {
      throw new UnauthorizedException("Authentication is required");
    }

    User currentUser =
        userService
            .getUserByUsername(principal.getName())
            .orElseThrow(() -> new UnauthorizedException("Current user not found"));

    if (!currentUser.getRole().getName().equals("ROLE_ADMIN")) {
      throw new ForbiddenException("You are not authorized to view entity logs.");
    }

    Page<ActivityLog> logs = activityLogService.getLogsForEntity(entityType, entityId, pageable);
    Page<ActivityLogResponse> response = logs.map(dtoMapper::toActivityLogResponse);

    return ResponseEntity.ok(response);
  }
}
