package org.example.projectbackendteammycodebasebringsalltheboys.security.oauth;

import java.util.Collections;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Role;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.RoleRepository;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;

  @Setter
  private OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate =
      new DefaultOAuth2UserService();

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) {
    OAuth2User oauthUser = delegate.loadUser(userRequest);

    String email = oauthUser.getAttribute("email");
    if (!StringUtils.hasText(email)) {
      throw new OAuth2AuthenticationException(
          new OAuth2Error("invalid_user_info"), "Email not provided by OAuth2 provider");
    }

    String userNameAttributeKey =
        userRequest
            .getClientRegistration()
            .getProviderDetails()
            .getUserInfoEndpoint()
            .getUserNameAttributeName();

    User user = findOrCreateUser(email);

    return new DefaultOAuth2User(
        Collections.singleton(new SimpleGrantedAuthority(user.getRole().getName())),
        oauthUser.getAttributes(),
        userNameAttributeKey);
  }

  private User findOrCreateUser(String email) {
    try {
      return userRepository.findByEmail(email).orElseGet(() -> createNewUser(email));
    } catch (DataIntegrityViolationException ex) {
      return userRepository.findByEmail(email).orElseThrow(() -> ex);
    }
  }

  private User createNewUser(String email) {
    Role defaultRole =
        roleRepository
            .findByName("ROLE_STUDENT")
            .orElseThrow(
                () ->
                    new OAuth2AuthenticationException(
                        new OAuth2Error("server_error"), "Default role not found"));

    User user = new User();
    user.setUsername(email);
    user.setEmail(email);
    user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
    user.setRole(defaultRole);

    return userRepository.save(user);
  }
}
