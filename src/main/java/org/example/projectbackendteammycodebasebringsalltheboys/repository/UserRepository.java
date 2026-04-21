package org.example.projectbackendteammycodebasebringsalltheboys.repository;

import java.util.Optional;
import java.util.UUID;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
  Optional<User> findByUsername(String username);

  Optional<User> findByEmail(String email);

  boolean existsByUsername(String username);

  boolean existsByEmail(String email);

  long countByRole_Name(String roleName);

  @Query(
      "SELECT u FROM User u WHERE "
          + "(:search IS NULL OR LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))) AND "
          + "(:roleName IS NULL OR u.role.name = :roleName)")
  Page<User> findBySearchAndRole(
      @Param("search") String search, @Param("roleName") String roleName, Pageable pageable);
}
