package org.example.projectbackendteammycodebasebringsalltheboys.controller;

import jakarta.validation.Valid;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.user.RegistrationRequest;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final UserService userService;

  public AuthController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping("/register")
  public ResponseEntity<?> registerUser(@Valid @RequestBody RegistrationRequest request) {
    try {
      User user = userService.registerUser(request);
      return ResponseEntity.ok().body(Map.of(
              "message", "User registered successfully",
              "username", user.getUsername()
      ));
    } catch (IllegalStateException e) {
      return ResponseEntity.badRequest().body(Map.of(
              "error", e.getMessage()
      ));
    }
  }

  @GetMapping("/me")
  public ResponseEntity<?> getCurrentUser(Authentication authentication) {
    if (authentication == null || !authentication.isAuthenticated()) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    String username = authentication.getName();
    User user = userService.getUserByUsername(username)
            .orElseThrow(() -> new IllegalStateException("User not found"));

    return ResponseEntity.ok().body(Map.of(
            "username", user.getUsername(),
            "role", user.getRole().getName(
            )));
  }
}