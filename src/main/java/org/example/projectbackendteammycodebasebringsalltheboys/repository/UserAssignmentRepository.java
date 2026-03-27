package org.example.projectbackendteammycodebasebringsalltheboys.repository;

import org.example.projectbackendteammycodebasebringsalltheboys.entity.Assignment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.UserAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAssignmentRepository extends JpaRepository<UserAssignment, Long> {
    List<UserAssignment> findByStudent(User student);
    Optional<UserAssignment> findByAssignmentAndStudent(Assignment assignment, User student);
}
