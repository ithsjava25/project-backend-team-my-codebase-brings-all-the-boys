package org.example.projectbackendteammycodebasebringsalltheboys.security.oauth;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Role;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.RoleRepository;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.JdkClientHttpRequestFactory;
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
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
  private static final Logger log = LoggerFactory.getLogger(CustomOAuth2UserService.class);

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;

  @Setter
  private OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate =
      new DefaultOAuth2UserService();

  private final RestClient restClient =
      RestClient.builder()
          .requestFactory(
              new JdkClientHttpRequestFactory(
                  java.net.http.HttpClient.newBuilder()
                      .connectTimeout(Duration.ofSeconds(5))
                      .build()))
          .build();

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) {
    OAuth2User oauthUser = delegate.loadUser(userRequest);

    String email = oauthUser.getAttribute("email");

    if (!StringUtils.hasText(email)) {
      email = fetchEmailFromGitHub(userRequest.getAccessToken().getTokenValue());
    }

    if (!StringUtils.hasText(email)) {
      throw new OAuth2AuthenticationException(
          new OAuth2Error("invalid_user_info"),
          "Email not provided by OAuth2 provider and could not be fetched");
    }

    String login = oauthUser.getAttribute("login");
    if (!StringUtils.hasText(login)) {
      throw new OAuth2AuthenticationException(
          new OAuth2Error("invalid_user_info"), "Login (username) not provided by OAuth2 provider");
    }

    User user = findOrCreateUser(email, login);

    return new DefaultOAuth2User(
        Collections.singleton(new SimpleGrantedAuthority(user.getRole().getName())),
        oauthUser.getAttributes(),
        "login");
  }

  private String fetchEmailFromGitHub(String accessToken) {
    try {
      List<Map<String, Object>> emails =
          restClient
              .get()
              .uri("https://api.github.com/user/emails")
              .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
              .header(HttpHeaders.ACCEPT, "application/vnd.github+json")
              .retrieve()
              .body(new ParameterizedTypeReference<>() {});

      if (emails == null) return null;

      // Prioritize primary verified email
      return emails.stream()
          .filter(
              e -> Boolean.TRUE.equals(e.get("primary")) && Boolean.TRUE.equals(e.get("verified")))
          .map(e -> (String) e.get("email"))
          .findFirst()
          // Fallback: first verified email
          .orElseGet(
              () ->
                  emails.stream()
                      .filter(e -> Boolean.TRUE.equals(e.get("verified")))
                      .map(e -> (String) e.get("email"))
                      .findFirst()
                      .orElse(null));
    } catch (Exception e) {
      log.warn("Failed to fetch email from GitHub: {}", e.getMessage());
      return null;
    }
  }

  private User findOrCreateUser(String email, String login) {
    try {
      return userRepository.findByEmail(email).orElseGet(() -> createNewUser(email, login));
    } catch (DataIntegrityViolationException ex) {
      return userRepository.findByEmail(email).orElseThrow(() -> ex);
    }
  }

  private User createNewUser(String email, String login) {
    Role defaultRole =
        roleRepository
            .findByName("ROLE_STUDENT")
            .orElseThrow(
                () ->
                    new OAuth2AuthenticationException(
                        new OAuth2Error("server_error"), "Default role not found"));

    User user = new User();
    user.setUsername(login);
    user.setEmail(email);
    user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
    user.setRole(defaultRole);

    return userRepository.save(user);
  }
}
