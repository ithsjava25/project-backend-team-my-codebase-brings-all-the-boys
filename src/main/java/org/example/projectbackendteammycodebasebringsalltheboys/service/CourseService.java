package org.example.projectbackendteammycodebasebringsalltheboys.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Course;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.SchoolClass;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.exception.NotFoundException;
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
      String name, String description, SchoolClass schoolClass, User leadTeacher, User creator) {
    Course course = new Course();
    course.setName(name);
    course.setDescription(description);
    course.setSchoolClass(schoolClass);
    course.setLeadTeacher(leadTeacher);

    Course saved = courseRepository.save(course);

    activityLogService.log(
        creator,
        "CREATED_COURSE",
        "Course",
        saved.getId(),
        "Course " + name + " created for class: " + schoolClass.getName());

    return saved;
  }

  @Transactional
  public void updateLeadTeacher(UUID courseId, User newLead, User updater) {
    Course course =
        courseRepository
            .findById(courseId)
            .orElseThrow(() -> new NotFoundException("Course not found"));
    course.setLeadTeacher(newLead);
    courseRepository.save(course);

    activityLogService.log(
        updater,
        "UPDATED_COURSE_LEAD",
        "Course",
        course.getId(),
        "Updated lead teacher to: " + newLead.getUsername());
  }

  @Transactional
  public void addAssistant(UUID courseId, User assistant, User updater) {
    Course course =
        courseRepository
            .findById(courseId)
            .orElseThrow(() -> new NotFoundException("Course not found"));
    boolean alreadyAssigned =
        course.getAssistants().stream().anyMatch(user -> user.getId().equals(assistant.getId()));
    if (!alreadyAssigned) {
      course.getAssistants().add(assistant);
      courseRepository.save(course);

      activityLogService.log(
          updater,
          "ADDED_COURSE_ASSISTANT",
          "Course",
          course.getId(),
          "Added assistant: " + assistant.getUsername());
    }
  }

  @Transactional
  public void removeAssistant(UUID courseId, User assistant, User updater) {
    Course course =
        courseRepository
            .findById(courseId)
            .orElseThrow(() -> new NotFoundException("Course not found"));
    if (course.getAssistants().removeIf(user -> user.getId().equals(assistant.getId()))) {
      courseRepository.save(course);

      activityLogService.log(
          updater,
          "REMOVED_COURSE_ASSISTANT",
          "Course",
          course.getId(),
          "Removed assistant: " + assistant.getUsername());
    }
  }

  @Transactional(readOnly = true)
  public List<Course> getCoursesByClass(SchoolClass schoolClass) {
    return courseRepository.findBySchoolClass(schoolClass);
  }

  @Transactional(readOnly = true)
  public Optional<Course> getCourseById(UUID id) {
    return courseRepository.findById(id);
  }

  @Transactional(readOnly = true)
  public List<Course> getAllCourses() {
    return courseRepository.findAll();
  }
}
