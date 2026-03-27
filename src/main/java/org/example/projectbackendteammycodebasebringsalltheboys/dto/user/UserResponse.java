package org.example.projectbackendteammycodebasebringsalltheboys.dto.user;

import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private RoleResponse role;
}
