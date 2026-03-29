package org.example.projectbackendteammycodebasebringsalltheboys.security.config;

import org.example.projectbackendteammycodebasebringsalltheboys.security.oauth.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, CustomOAuth2UserService customOAuth2UserService) throws Exception {

        http
                .csrf(Customizer.withDefaults())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/login", "/auth/register", "/auth/logout-success").permitAll()
                        .requestMatchers("/oauth2/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )

                .formLogin(form -> form
                        .loginPage("/auth/login")
                        .permitAll()
                )

                .oauth2Login(oauth -> oauth
                        .loginPage("/auth/login")
                        .userInfoEndpoint(info -> info.userService(customOAuth2UserService))
                        .defaultSuccessUrl("/", true)
                )

                .logout(logout -> logout
                        .logoutSuccessUrl("/auth/login?logout")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
