package org.example.projectbackendteammycodebasebringsalltheboys.security.config;

import jakarta.servlet.http.HttpServletResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.security.oauth.CustomOAuth2UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

  @Value("${frontend.url}")
  private String frontendUrl;

  @Bean
  public SecurityFilterChain filterChain(
      HttpSecurity http,
      CustomOAuth2UserService customOAuth2UserService,
      OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler) {

    http.csrf(
            csrf -> {
              CsrfTokenRequestAttributeHandler handler = new CsrfTokenRequestAttributeHandler();
              handler.setCsrfRequestAttributeName(null); // Deactivates deferred token loading
              csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                  .csrfTokenRequestHandler(handler)
                  .ignoringRequestMatchers("/api/auth/register", "/oauth2/**", "/api/csrf-token");
            })
        .cors(Customizer.withDefaults()) // Removed this line to rely solely on CorsConfig bean
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers("/api/auth/me")
                    .permitAll()
                    .requestMatchers("/api/auth/register")
                    .permitAll()
                    .requestMatchers("/api/auth/login")
                    .permitAll()
                    .requestMatchers("/api/csrf-token")
                    .permitAll()
                    .requestMatchers("/error")
                    .permitAll()
                    .requestMatchers("/oauth2/**")
                    .permitAll()
                    .requestMatchers("/admin/**")
                    .hasRole("ADMIN")
                    .requestMatchers("/api/**")
                    .authenticated()
                    .anyRequest()
                    .authenticated())
        .exceptionHandling(
            ex ->
                ex.authenticationEntryPoint(
                    (req, response, authException) ->
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")))
        .oauth2Login(
            oauth ->
                oauth
                    .userInfoEndpoint(info -> info.userService(customOAuth2UserService))
                    .successHandler(oAuth2LoginSuccessHandler))
        .logout(
            logout ->
                logout
                    .logoutUrl("/api/auth/logout")
                    .logoutSuccessUrl(frontendUrl)
                    .invalidateHttpSession(true)
                    .clearAuthentication(true)
                    .deleteCookies("JSESSIONID"));

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) {
    return config.getAuthenticationManager();
  }
}
