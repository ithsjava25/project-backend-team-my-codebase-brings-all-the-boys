package org.example.projectbackendteammycodebasebringsalltheboys.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.Map;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.user.ExternalRegistrationRequest;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.user.UserResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final UserService userService;
  private final AuthenticationManager authenticationManager;
  private final HttpSessionSecurityContextRepository securityContextRepository =
      new HttpSessionSecurityContextRepository();

  public AuthController(UserService userService, AuthenticationManager authenticationManager) {
    this.userService = userService;
    this.authenticationManager = authenticationManager;
  }

  @PostMapping("/register")
  public ResponseEntity<?> registerUser(@Valid @RequestBody ExternalRegistrationRequest request) {
    try {
      User user = userService.externalUserRegistration(request);
      UserResponse response = userService.toUserResponse(user);

      return ResponseEntity.status(HttpStatus.CREATED).body(response);
    } catch (IllegalStateException e) {
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(
      @RequestBody Map<String, String> credentials,
      HttpServletRequest request,
      HttpServletResponse response) {

    String username = credentials.get("username");
    String password = credentials.get("password");

    if (username == null || username.isBlank() || password == null || password.isBlank()) {
      return ResponseEntity.badRequest()
          .body(Map.of("error", "Username and password are required"));
    }

    try {
      UsernamePasswordAuthenticationToken token =
          new UsernamePasswordAuthenticationToken(username, password);

      Authentication auth = authenticationManager.authenticate(token);
      SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
      securityContext.setAuthentication(auth);
      SecurityContextHolder.setContext(securityContext);
      securityContextRepository.saveContext(securityContext, request, response);

      return ResponseEntity.ok(Map.of("message", "Logged in"));
    } catch (AuthenticationException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(Map.of("error", "Wrong username or password"));
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
}
