package org.example.projectbackendteammycodebasebringsalltheboys.repository;

import java.util.Optional;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.SchoolClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SchoolClassRepository extends JpaRepository<SchoolClass, Long> {
  Optional<SchoolClass> findByName(String name);
}
