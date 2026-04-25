package org.example.projectbackendteammycodebasebringsalltheboys.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.user.UserProfileResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.user.UserRequest;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.user.UserResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.mapper.DtoMapper;
import org.example.projectbackendteammycodebasebringsalltheboys.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class PublicUserController {

  private final UserService userService;
  private final DtoMapper dtoMapper;

  @GetMapping("/teachers")
  @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
  public ResponseEntity<List<UserResponse>> getTeachers() {
    return ResponseEntity.ok(
        userService.getUsersByRole("ROLE_TEACHER").stream()
            .map(dtoMapper::toUserResponse)
            .collect(Collectors.toList()));
  }

  @GetMapping("/students")
  @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
  public ResponseEntity<List<UserResponse>> getStudents() {
    return ResponseEntity.ok(
        userService.getUsersByRole("ROLE_STUDENT").stream()
            .map(dtoMapper::toUserResponse)
            .collect(Collectors.toList()));
  }

  @GetMapping("/profile/{id}")
  public ResponseEntity<UserProfileResponse> getUserProfile(@PathVariable UUID id) {
    return ResponseEntity.ok(userService.getUserProfile(id));
  }

  @GetMapping("/me")
  public ResponseEntity<UserProfileResponse> getMyProfile() {
    return ResponseEntity.ok(userService.getUserProfile(userService.getCurrentUser().getId()));
  }

  @PutMapping("/profile")
  public ResponseEntity<UserResponse> updateProfile(@RequestBody UserRequest request) {
    return ResponseEntity.ok(dtoMapper.toUserResponse(userService.updateProfile(request)));
  }
}
