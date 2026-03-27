package org.example.projectbackendteammycodebasebringsalltheboys.service;

import org.example.projectbackendteammycodebasebringsalltheboys.dto.user.RegistrationRequest;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Role;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.RoleRepository;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldRegisterNewUser() {
        RegistrationRequest request = new RegistrationRequest();
        request.setUsername("test@example.com");
        request.setPassword("password123");
        request.setConfirmPassword("password123");

        when(userRepository.findByUsername("test@example.com"))
                .thenReturn(Optional.empty());

        Role role = new Role();
        role.setName("ROLE_STUDENT");

        when(roleRepository.findByName("ROLE_STUDENT"))
                .thenReturn(Optional.of(role));

        when(passwordEncoder.encode("password123"))
                .thenReturn("encoded");

        when(userRepository.save(any(User.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        User user = userService.registerUser(request);

        assertEquals("test@example.com", user.getUsername());
        assertEquals("encoded", user.getPassword());
        assertEquals(role, user.getRole());
    }

    @Test
    void shouldThrowIfUserExists() {
        RegistrationRequest request = new RegistrationRequest();
        request.setUsername("test@example.com");

        when(userRepository.findByUsername("test@example.com"))
                .thenReturn(Optional.of(new User()));

        assertThrows(IllegalStateException.class,
                () -> userService.registerUser(request));
    }
}
