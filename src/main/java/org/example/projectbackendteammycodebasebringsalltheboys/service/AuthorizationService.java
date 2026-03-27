package org.example.projectbackendteammycodebasebringsalltheboys.service;

import lombok.RequiredArgsConstructor;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Assignment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.UserAssignmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthorizationService {

    private final UserAssignmentRepository userAssignmentRepository;

    @Transactional(readOnly = true)
    public boolean canAccessCase(User user, Assignment assignment) {
        // Admin can access everything
        if (isAdmin(user)) return true;

        // Teacher who created the case
        if (isTeacher(user) && assignment.getCreator().getId().equals(user.getId())) {
            return true;
        }

        // Student who is assigned the case
        return isStudent(user) &&
                userAssignmentRepository.findByAssignmentAndStudent(assignment, user).isPresent();
    }

    @Transactional(readOnly = true)
    public boolean canManageCase(User user, Assignment assignment) {
        return isAdmin(user) ||
                (isTeacher(user) && assignment.getCreator().getId().equals(user.getId()));
    }

    private boolean isAdmin(User user) {
        return user.getRole().getName().equalsIgnoreCase("ROLE_ADMIN");
    }

    private boolean isTeacher(User user) {
        return user.getRole().getName().equalsIgnoreCase("ROLE_TEACHER");
    }

    private boolean isStudent(User user) {
        return user.getRole().getName().equalsIgnoreCase("ROLE_STUDENT");
    }
}
