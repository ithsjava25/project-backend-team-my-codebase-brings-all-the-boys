package org.example.projectbackendteammycodebasebringsalltheboys.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Course;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.SchoolClass;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.exception.BadRequestException;
import org.example.projectbackendteammycodebasebringsalltheboys.exception.NotFoundException;
import org.example.projectbackendteammycodebasebringsalltheboys.mapper.DtoMapper;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.CourseRepository;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.SchoolClassRepository;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

  @Mock private CourseRepository courseRepository;
  @Mock private ActivityLogService activityLogService;
  @Mock private ClassEnrollmentService classEnrollmentService;
  @Mock private DtoMapper dtoMapper;
  @Mock private SchoolClassRepository schoolClassRepository;
  @Mock private UserRepository userRepository;
  @Mock private AuthorizationService authorizationService;

  private CourseService courseService;

  @BeforeEach
  void setUp() {
    courseService =
        new CourseService(
            courseRepository,
            activityLogService,
            classEnrollmentService,
            dtoMapper,
            schoolClassRepository,
            userRepository,
            authorizationService);
  }

  @Test
  @DisplayName("createCourse throws BadRequestException if schoolClass is null")
  void createCourse_nullClass_throwsException() {
    LocalDateTime endDate = LocalDateTime.now().plusDays(1);
    assertThatThrownBy(
            () -> courseService.createCourse("Name", "Desc", null, new User(), new User(), endDate))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("SchoolClass cannot be null");
  }

  @Test
  @DisplayName("createCourse saves course when valid")
  void createCourse_valid_savesCourse() {
    LocalDateTime endDate = LocalDateTime.now().plusDays(1);
    SchoolClass schoolClass = new SchoolClass();
    User leadTeacher = new User();
    User creator = new User();
    when(authorizationService.canCreateCourseInClass(any(), any())).thenReturn(true);
    when(authorizationService.isMemberOfClass(eq(leadTeacher), any())).thenReturn(true);
    when(courseRepository.save(any(Course.class))).thenAnswer(inv -> inv.getArgument(0));

    Course result =
        courseService.createCourse("Math", "Algebra", schoolClass, leadTeacher, creator, endDate);

    assertThat(result.getName()).isEqualTo("Math");
    assertThat(result.getSchoolClass()).isEqualTo(schoolClass);
    assertThat(result.getLeadTeacher()).isEqualTo(leadTeacher);
    verify(courseRepository).save(any(Course.class));
  }

  @Test
  @DisplayName("updateLeadTeacher updates and saves course")
  void updateLeadTeacher_valid_updatesCourse() {
    UUID courseId = UUID.randomUUID();
    User newLead = new User();
    newLead.setId(UUID.randomUUID());
    User updater = new User();
    updater.setId(UUID.randomUUID());
    Course course = new Course();
    course.setId(courseId);
    course.setSchoolClass(new SchoolClass());

    when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
    when(authorizationService.canModifyCourse(any(), any())).thenReturn(true);
    when(authorizationService.isMemberOfClass(any(), any())).thenReturn(true);

    courseService.updateLeadTeacher(courseId, newLead, updater);

    assertThat(course.getLeadTeacher()).isEqualTo(newLead);
    verify(courseRepository).save(course);
  }

  @Test
  @DisplayName("updateLeadTeacher throws NotFoundException if course missing")
  void updateLeadTeacher_notFound_throwsException() {
    UUID id = UUID.randomUUID();
    User user = new User();
    user.setId(UUID.randomUUID());
    when(courseRepository.findById(id)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> courseService.updateLeadTeacher(id, user, user))
        .isInstanceOf(NotFoundException.class);
  }

  @Test
  @DisplayName("addAssistant adds assistant and logs activity")
  void addAssistant_valid_addsAndLogs() {
    UUID courseId = UUID.randomUUID();
    User assistant = new User();
    assistant.setId(UUID.randomUUID());
    assistant.setUsername("assistant1");
    User updater = new User();
    updater.setId(UUID.randomUUID());
    Course course = new Course();
    course.setId(courseId);
    course.setAssistants(new ArrayList<>());

    when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

    courseService.addAssistant(courseId, assistant, updater);

    assertThat(course.getAssistants()).contains(assistant);
    verify(courseRepository).save(course);
    verify(activityLogService).log(eq(updater), eq(courseId), any(), any(), any(), any(), any());
  }

  @Test
  @DisplayName("addAssistant does nothing if already assistant")
  void addAssistant_alreadyPresent_doesNothing() {
    UUID courseId = UUID.randomUUID();
    User assistant = new User();
    assistant.setId(UUID.randomUUID());
    Course course = new Course();
    course.setAssistants(new ArrayList<>(List.of(assistant)));

    when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

    User updater = new User();
    updater.setId(UUID.randomUUID());
    courseService.addAssistant(courseId, assistant, updater);

    verify(courseRepository, never()).save(any());
    verifyNoInteractions(activityLogService);
  }
}
