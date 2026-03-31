package org.example.projectbackendteammycodebasebringsalltheboys.repository;

import java.util.List;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Course;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.SchoolClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
  List<Course> findBySchoolClass(SchoolClass schoolClass);
}
