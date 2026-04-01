package org.example.projectbackendteammycodebasebringsalltheboys.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.Map;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.user.RegistrationRequest;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.user.UserResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final UserService userService;
  private final AuthenticationManager authenticationManager;

  public AuthController(UserService userService, AuthenticationManager authenticationManager) {
    this.userService = userService;
    this.authenticationManager = authenticationManager;
  }

  @PostMapping("/register")
  public ResponseEntity<?> registerUser(@Valid @RequestBody RegistrationRequest request) {
    try {
      User user = userService.registerUser(request);
      UserResponse response = userService.toUserResponse(user);

      return ResponseEntity.ok(response);
    } catch (IllegalStateException e) {
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(
      @RequestBody Map<String, String> credentials, HttpServletRequest request) {
    try {
      UsernamePasswordAuthenticationToken token =
          new UsernamePasswordAuthenticationToken(
              credentials.get("username"), credentials.get("password"));

      Authentication auth = authenticationManager.authenticate(token);
      SecurityContextHolder.getContext().setAuthentication(auth);
      request.getSession(true);

      return ResponseEntity.ok(Map.of("message", "Inloggad"));
    } catch (AuthenticationException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(Map.of("error", "Fel användarnamn eller lösenord"));
    }
  }

  @GetMapping("/me")
  public ResponseEntity<?> getCurrentUser(Authentication authentication) {
    if (authentication == null || !authentication.isAuthenticated()) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    String username = authentication.getName();
    User user =
        userService
            .getUserByUsername(username)
            .orElseThrow(() -> new IllegalStateException("User not found: " + username));

    return ResponseEntity.ok(userService.toUserResponse(user));
  }

  @PostMapping("/logout")
  public ResponseEntity<?> logout(HttpServletRequest request) {
    HttpSession session = request.getSession(false);
    if (session != null) session.invalidate();
    return ResponseEntity.ok(Map.of("message", "Utloggad"));
  }
}
