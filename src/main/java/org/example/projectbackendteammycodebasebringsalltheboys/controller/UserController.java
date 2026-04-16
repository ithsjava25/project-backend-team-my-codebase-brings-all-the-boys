package org.example.projectbackendteammycodebasebringsalltheboys.controller;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.user.UserRequest;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.user.UserResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.exception.NotFoundException;
import org.example.projectbackendteammycodebasebringsalltheboys.mapper.DtoMapper;
import org.example.projectbackendteammycodebasebringsalltheboys.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") // Restrict all endpoints in this controller to ADMIN role
public class UserController {

  private final UserService userService;
  private final DtoMapper dtoMapper;

  @GetMapping
  public ResponseEntity<Page<UserResponse>> getAllUsers(Pageable pageable) {
    Page<User> users = userService.findAllUsers(pageable);
    Page<UserResponse> response = users.map(dtoMapper::toUserResponse);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
    User user =
        userService
            .getUserById(id)
            .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
    return ResponseEntity.ok(dtoMapper.toUserResponse(user));
  }

  @PostMapping
  public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest request) {
    User user = userService.createUser(request);
    UserResponse response = dtoMapper.toUserResponse(user);
    // Set location header for the newly created resource
    var uri =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(user.getId())
            .toUri();
    return ResponseEntity.created(uri).body(response);
  }

  @PutMapping("/{id}")
  public ResponseEntity<UserResponse> updateUser(
      @PathVariable UUID id, @RequestBody UserRequest request) {
    User updatedUser = userService.updateUser(id, request);
    return ResponseEntity.ok(dtoMapper.toUserResponse(updatedUser));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
    userService.deleteUser(id);
    return ResponseEntity.noContent().build();
  }
}
