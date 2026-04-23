package org.example.projectbackendteammycodebasebringsalltheboys.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.dashboard.DashboardStatsResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.dashboard.PendingSubmissionDTO;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.dashboard.UpcomingDeadlineDTO;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.exception.UnauthorizedException;
import org.example.projectbackendteammycodebasebringsalltheboys.service.DashboardService;
import org.example.projectbackendteammycodebasebringsalltheboys.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

  private final DashboardService dashboardService;
  private final UserService userService;

  @GetMapping("/stats")
  public ResponseEntity<DashboardStatsResponse> getStats(java.security.Principal principal) {
    User user = resolveUser(principal);
    return ResponseEntity.ok(dashboardService.getStats(user));
  }

  @GetMapping("/pending-submissions")
  @PreAuthorize("hasAnyAuthority('ROLE_TEACHER', 'ROLE_ADMIN')")
  public ResponseEntity<List<PendingSubmissionDTO>> getPendingSubmissions(
      java.security.Principal principal) {
    User user = resolveUser(principal);

    return ResponseEntity.ok(dashboardService.getPendingSubmissions(user));
  }

  @GetMapping("/upcoming-deadlines")
  public ResponseEntity<List<UpcomingDeadlineDTO>> getUpcomingDeadlines(
      java.security.Principal principal) {
    User user = resolveUser(principal);
    return ResponseEntity.ok(dashboardService.getUpcomingDeadlines(user));
  }

  private User resolveUser(java.security.Principal principal) {
    if (principal == null) {
      throw new UnauthorizedException("Authentication is required");
    }
    return userService
        .getUserByUsername(principal.getName())
        .orElseThrow(() -> new UnauthorizedException("User not found"));
  }
}
