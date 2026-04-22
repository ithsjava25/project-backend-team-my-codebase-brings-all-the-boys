package org.example.projectbackendteammycodebasebringsalltheboys.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.projectbackendteammycodebasebringsalltheboys.annotation.LogActivity;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.course.CourseDetailResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.course.CourseSurfaceResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Course;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.SchoolClass;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.ActivityAction;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.ActivityStatus;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.EntityType;
import org.example.projectbackendteammycodebasebringsalltheboys.exception.BadRequestException;
import org.example.projectbackendteammycodebasebringsalltheboys.exception.ForbiddenException;
import org.example.projectbackendteammycodebasebringsalltheboys.exception.NotFoundException;
import org.example.projectbackendteammycodebasebringsalltheboys.mapper.DtoMapper;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.CourseRepository;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.SchoolClassRepository;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.UserRepository;
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
  private final DtoMapper dtoMapper;
  private final SchoolClassRepository schoolClassRepository;
  private final UserRepository userRepository;
  private final AuthorizationService authorizationService;

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

    if (!authorizationService.canCreateCourseInClass(creator, schoolClass)) {
      throw new ForbiddenException("You are not authorized to create courses in this class.");
    }

    // Optional: Validate lead teacher belongs to the class too
    if (leadTeacher != null && !authorizationService.isMemberOfClass(leadTeacher, schoolClass)) {
      throw new BadRequestException("Lead teacher must be a member of the school class.");
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

    if (!authorizationService.canModifyCourse(updater, course)) {
      throw new ForbiddenException("You are not authorized to modify this course.");
    }

    if (!authorizationService.isMemberOfClass(newLead, course.getSchoolClass())) {
      throw new BadRequestException("Lead teacher must be a member of the school class.");
    }

    course.setLeadTeacher(newLead);
    courseRepository.save(course);
  }

  @Transactional
  public CourseDetailResponse updateCourse(
      UUID id,
      org.example.projectbackendteammycodebasebringsalltheboys.dto.course.CourseUpdateRequest
          request,
      User updater) {
    Course course =
        courseRepository.findById(id).orElseThrow(() -> new NotFoundException("Course not found"));

    if (!authorizationService.canModifyCourse(updater, course)) {
      throw new ForbiddenException("You are not authorized to modify this course.");
    }

    java.util.List<String> updatedFields = new java.util.ArrayList<>();

    if (request.getName() != null) {
      course.setName(request.getName());
      updatedFields.add("name");
    }
    if (request.getDescription() != null) {
      course.setDescription(request.getDescription());
      updatedFields.add("description");
    }
    if (request.getEndDate() != null) {
      course.setEndDate(request.getEndDate());
      updatedFields.add("endDate");
    }

    if (request.getSchoolClassId() != null) {
      SchoolClass sc =
          schoolClassRepository
              .findById(request.getSchoolClassId())
              .orElseThrow(() -> new NotFoundException("School Class not found"));

      // Check if user can move course to another class (create permission in target class)
      if (!course.getSchoolClass().getId().equals(sc.getId())
          && !authorizationService.canCreateCourseInClass(updater, sc)) {
        throw new ForbiddenException("You are not authorized to move course to this class.");
      }

      course.setSchoolClass(sc);
      updatedFields.add("schoolClass");

      // Verify existing lead teacher is valid for new class,
      // unless we are also updating the lead teacher in this request.
      if (request.getLeadTeacherId() == null && course.getLeadTeacher() != null) {
        if (!authorizationService.isMemberOfClass(course.getLeadTeacher(), sc)) {
          throw new BadRequestException("Lead teacher must be a member of the school class.");
        }
      }
    }

    if (request.getLeadTeacherId() != null) {
      User teacher =
          userRepository
              .findById(request.getLeadTeacherId())
              .orElseThrow(() -> new NotFoundException("Lead Teacher not found"));

      if (!authorizationService.isMemberOfClass(teacher, course.getSchoolClass())) {
        throw new BadRequestException("Lead teacher must be a member of the school class.");
      }

      course.setLeadTeacher(teacher);
      updatedFields.add("leadTeacher");
    }

    Course saved = courseRepository.save(course);

    java.util.Map<String, Object> details = new java.util.HashMap<>();
    details.put("name", saved.getName());
    details.put("updatedFields", String.join(",", updatedFields));

    activityLogService.log(
        updater,
        saved.getId(),
        ActivityAction.UPDATED,
        EntityType.COURSE,
        null,
        details,
        ActivityStatus.SUCCESS);

    return dtoMapper.toCourseDetailResponse(saved);
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
  public void deleteCourse(UUID id, User updater) {
    Course course =
        courseRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Course not found with id: " + id));

    if (!authorizationService.canModifyCourse(updater, course)) {
      throw new ForbiddenException("You are not authorized to delete this course.");
    }

    java.util.Map<String, Object> details = new java.util.HashMap<>();
    details.put("name", course.getName());
    details.put("deletedCourseId", id.toString());

    activityLogService.log(
        updater,
        id,
        ActivityAction.DELETED,
        EntityType.COURSE,
        null,
        details,
        ActivityStatus.SUCCESS);

    courseRepository.delete(course);
  }

  // Filtered course list
  @Transactional(readOnly = true)
  public Page<CourseSurfaceResponse> getAccessibleCoursesDto(User user, Pageable pageable) {
    return getAccessibleCourses(user, pageable).map(dtoMapper::toCourseSurfaceResponse);
  }

  // Get course if access
  @Transactional(readOnly = true)
  public Optional<CourseDetailResponse> getAccessibleCourseDto(UUID id, User user) {
    return getAccessibleCourse(id, user).map(dtoMapper::toCourseDetailResponse);
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
