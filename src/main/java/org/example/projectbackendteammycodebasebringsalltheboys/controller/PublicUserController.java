package org.example.projectbackendteammycodebasebringsalltheboys.controller;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.user.UserProfileResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class PublicUserController {

  private final UserService userService;

  @GetMapping("/profile/{id}")
  public ResponseEntity<UserProfileResponse> getUserProfile(@PathVariable UUID id) {
    return ResponseEntity.ok(userService.getUserProfile(id));
  }

  @GetMapping("/me")
  public ResponseEntity<UserProfileResponse> getMyProfile() {
    return ResponseEntity.ok(userService.getUserProfile(userService.getCurrentUser().getId()));
  }
}
