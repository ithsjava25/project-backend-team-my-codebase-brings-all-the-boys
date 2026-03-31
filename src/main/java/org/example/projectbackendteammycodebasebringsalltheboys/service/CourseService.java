package org.example.projectbackendteammycodebasebringsalltheboys.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Course;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.SchoolClass;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.CourseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CourseService {

  private final CourseRepository courseRepository;
  private final ActivityLogService activityLogService;

  @Transactional
  public Course createCourse(
      String name, String description, SchoolClass schoolClass, User creator) {
    Course course = new Course();
    course.setName(name);
    course.setDescription(description);
    course.setSchoolClass(schoolClass);

    Course saved = courseRepository.save(course);

    activityLogService.log(
        creator,
        "CREATED_COURSE",
        "Course",
        saved.getId(),
        "Course " + name + " created for class: " + schoolClass.getName());

    return saved;
  }

  @Transactional(readOnly = true)
  public List<Course> getCoursesByClass(SchoolClass schoolClass) {
    return courseRepository.findBySchoolClass(schoolClass);
  }

  @Transactional(readOnly = true)
  public Optional<Course> getCourseById(Long id) {
    return courseRepository.findById(id);
  }
}
