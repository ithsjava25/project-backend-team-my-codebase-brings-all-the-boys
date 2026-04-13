package org.example.projectbackendteammycodebasebringsalltheboys.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.projectbackendteammycodebasebringsalltheboys.annotation.LogActivity;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Course;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.SchoolClass;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.ActivityAction;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.ActivityStatus;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.EntityType;
import org.example.projectbackendteammycodebasebringsalltheboys.exception.BadRequestException;
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
    return createCourse(name, description, schoolClass, leadTeacher, creator, null);
  }

  @LogActivity(
      action = ActivityAction.CREATED,
      entityType = EntityType.COURSE,
      orphan = true,
      actorParamIndex = 5)
  @Transactional
  public Course createCourse(
      String name,
      String description,
      SchoolClass schoolClass,
      User leadTeacher,
      User creator,
      java.time.LocalDateTime endDate) {
    if (schoolClass == null) {
      throw new BadRequestException("SchoolClass cannot be null");
    }
    Course course = new Course();
    course.setName(name);
    course.setDescription(description);
    course.setSchoolClass(schoolClass);
    course.setLeadTeacher(leadTeacher);
    course.setEndDate(endDate);

    return courseRepository.save(course);
  }

  @LogActivity(
      action = ActivityAction.UPDATED,
      entityType = EntityType.COURSE,
      parentIdParamIndex = 0,
      actorParamIndex = 2)
  @Transactional
  public void updateLeadTeacher(UUID courseId, User newLead, User updater) {
    if (newLead == null || newLead.getId() == null) {
      throw new BadRequestException("Lead teacher is required");
    }
    if (updater == null || updater.getId() == null) {
      throw new BadRequestException("Updater is required");
    }
    Course course =
        courseRepository
            .findById(courseId)
            .orElseThrow(() -> new NotFoundException("Course not found"));
    course.setLeadTeacher(newLead);
    courseRepository.save(course);
  }

  @Transactional
  public void addAssistant(UUID courseId, User assistant, User updater) {
    if (assistant == null || assistant.getId() == null) {
      throw new BadRequestException("Assistant is required");
    }
    if (updater == null || updater.getId() == null) {
      throw new BadRequestException("Updater is required");
    }
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
          course.getId(),
          ActivityAction.ADDED,
          EntityType.COURSE,
          null,
          Map.of("addedAssistant", assistant.getUsername()),
          ActivityStatus.SUCCESS);
    }
  }

  @Transactional
  public void removeAssistant(UUID courseId, User assistant, User updater) {
    if (assistant == null || assistant.getId() == null) {
      throw new BadRequestException("Assistant is required");
    }
    if (updater == null || updater.getId() == null) {
      throw new BadRequestException("Updater is required");
    }
    Course course =
        courseRepository
            .findById(courseId)
            .orElseThrow(() -> new NotFoundException("Course not found"));
    if (course.getAssistants().removeIf(user -> user.getId().equals(assistant.getId()))) {
      courseRepository.save(course);

      activityLogService.log(
          updater,
          course.getId(),
          ActivityAction.REMOVED,
          EntityType.COURSE,
          null,
          Map.of("removedAssistant", assistant.getUsername()),
          ActivityStatus.SUCCESS);
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
