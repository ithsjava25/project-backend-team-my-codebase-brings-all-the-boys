package org.example.projectbackendteammycodebasebringsalltheboys.security;

import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.exception.UnauthorizedException;
import org.example.projectbackendteammycodebasebringsalltheboys.service.UserService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurrentUserResolver {

  private final UserService userService;

  public User resolveCurrentUser(Principal principal) {
    if (principal == null) {
      throw new UnauthorizedException("Authentication is required");
    }

    return userService
        .getUserByUsername(principal.getName())
        .orElseThrow(() -> new UnauthorizedException("Current user not found"));
  }
}
