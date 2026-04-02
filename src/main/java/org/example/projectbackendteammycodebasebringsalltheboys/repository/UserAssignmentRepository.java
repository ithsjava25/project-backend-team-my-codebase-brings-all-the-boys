package org.example.projectbackendteammycodebasebringsalltheboys.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Assignment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.UserAssignment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAssignmentRepository extends JpaRepository<UserAssignment, UUID> {
  List<UserAssignment> findByStudent(User student);

  Optional<UserAssignment> findByAssignmentAndStudent(Assignment assignment, User student);

  @EntityGraph(attributePaths = {"assignment", "student"})
  List<UserAssignment> findByStudentWithDetails(User student);
}
