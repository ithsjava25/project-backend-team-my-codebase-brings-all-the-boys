package org.example.projectbackendteammycodebasebringsalltheboys.security.config;

import org.example.projectbackendteammycodebasebringsalltheboys.security.oauth.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(
      HttpSecurity http, CustomOAuth2UserService customOAuth2UserService) {

    http.csrf(AbstractHttpConfigurer::disable)
        .cors(Customizer.withDefaults())
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers("/api/auth/me")
                    .permitAll()
                    .requestMatchers("/oauth2/**")
                    .permitAll()
                    .requestMatchers("/admin/**")
                    .hasRole("ADMIN")
                    .requestMatchers("/api/**")
                    .authenticated()
                    .anyRequest()
                    .authenticated())
        .oauth2Login(
            oauth ->
                oauth
                    .userInfoEndpoint(info -> info.userService(customOAuth2UserService))
                    .defaultSuccessUrl("http://localhost:5173/dashboard", true))
        .logout(logout -> logout.logoutSuccessUrl("http://localhost:5173").permitAll())
        .httpBasic(Customizer.withDefaults());

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
