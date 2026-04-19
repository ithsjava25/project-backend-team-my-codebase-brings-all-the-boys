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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CourseService {

  private final CourseRepository courseRepository;
  private final ActivityLogService activityLogService;
  private final ClassEnrollmentService enrollmentService;

  @Transactional
  @SuppressWarnings("unused")
  @LogActivity(
      action = ActivityAction.CREATED,
      entityType = EntityType.COURSE,
      orphan = true,
      actorParamIndex = 4)
  public Course createCourse(
      String name, String description, SchoolClass schoolClass, User leadTeacher, User creator) {
    return createCourse(name, description, schoolClass, leadTeacher, creator, null);
  }

  @LogActivity(
      action = ActivityAction.CREATED,
      entityType = EntityType.COURSE,
      orphan = true,
      actorParamIndex = 4)
  @Transactional
  @SuppressWarnings({"unused", "ConstantConditions"})
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

  @LogActivity(action = ActivityAction.UPDATED, entityType = EntityType.COURSE, actorParamIndex = 2)
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

  @SuppressWarnings("unused")
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

  @SuppressWarnings("unused")
  @Transactional(readOnly = true)
  public List<Course> getCoursesByClass(SchoolClass schoolClass) {
    return courseRepository.findBySchoolClass(schoolClass);
  }

  @Transactional(readOnly = true)
  public Optional<Course> getCourseById(UUID id) {
    return courseRepository.findById(id);
  }

  @Transactional(readOnly = true)
  public Page<Course> getAllCourses(Pageable pageable) {
    return courseRepository.findAll(pageable);
  }

  @Transactional
  @SuppressWarnings("unused")
  @LogActivity(action = ActivityAction.UPDATED, entityType = EntityType.COURSE, actorParamIndex = 1)
  public Course updateCourse(Course course) {
    return courseRepository.save(course);
  }

  @Transactional
  @LogActivity(action = ActivityAction.DELETED, entityType = EntityType.COURSE, actorParamIndex = 1)
  public void deleteCourse(UUID id, User updater) {
    if (!courseRepository.existsById(id)) {
      throw new NotFoundException("Course not found with id: " + id);
    }

    activityLogService.log(
        updater,
        id,
        ActivityAction.DELETED,
        EntityType.COURSE,
        null,
        Map.of("deletedCourseId", id.toString()),
        ActivityStatus.SUCCESS);

    courseRepository.deleteById(id);
  }

  // Filtered course list
  @Transactional(readOnly = true)
  public Page<Course> getAccessibleCourses(User user, Pageable pageable) {
    if (user == null) {
      return Page.empty();
    }
    return switch (user.getRole().getName()) {
      case "ROLE_STUDENT" -> getStudentCourses(user, pageable);
      case "ROLE_TEACHER" -> getTeacherCourses(user, pageable);
      case "ROLE_ADMIN" -> getAllCourses(pageable);
      default -> Page.empty();
    };
  }

  // Get course if access
  @Transactional(readOnly = true)
  public Optional<Course> getAccessibleCourse(UUID id, User user) {
    if (user == null) {
      return Optional.empty();
    }

    Optional<Course> course = courseRepository.findById(id);
    if (course.isEmpty()) {
      return Optional.empty();
    }

    // Check for access
    if (!hasAccess(course.get(), user)) {
      return Optional.empty();
    }

    return course;
  }

  // Helper methods

  private Page<Course> getStudentCourses(User student, Pageable pageable) {
    return courseRepository.findByEnrollments_UserId(student.getId(), pageable);
  }

  private Page<Course> getTeacherCourses(User teacher, Pageable pageable) {
    // TODO: Merge with assistants - for now just return lead courses
    return courseRepository.findByLeadTeacherId(teacher.getId(), pageable);
  }

  private boolean hasAccess(Course course, User user) {
    switch (user.getRole().getName()) {
      case "ROLE_ADMIN" -> {
        return true;
      }
      case "ROLE_TEACHER" -> {
        boolean isLead =
            course.getLeadTeacher() != null && course.getLeadTeacher().getId().equals(user.getId());
        boolean isAssistant =
            course.getAssistants().stream().anyMatch(a -> a.getId().equals(user.getId()));
        return isLead || isAssistant;
      }
      case "ROLE_STUDENT" -> {
        return enrollmentService.isUserInClass(user, course.getSchoolClass());
      }
    }

    return false;
  }
}
