package org.example.projectbackendteammycodebasebringsalltheboys.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.*;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.AssignmentStatus;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.ClassRole;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.StudentAssignmentStatus;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

  private final RoleRepository roleRepository;
  private final UserRepository userRepository;
  private final SchoolClassRepository schoolClassRepository;
  private final CourseRepository courseRepository;
  private final AssignmentRepository assignmentRepository;
  private final ClassEnrollmentRepository classEnrollmentRepository;
  private final UserAssignmentRepository userAssignmentRepository;
  private final CommentRepository commentRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  @Transactional
  public void run(String... args) {
    log.info("Starting data seeding for dev profile...");

    // 1. Roles
    Role adminRole = getOrCreateRole("ROLE_ADMIN");
    Role teacherRole = getOrCreateRole("ROLE_TEACHER");
    Role studentRole = getOrCreateRole("ROLE_STUDENT");

    // 2. Users
    User admin = getOrCreateUser("admin", "admin@school.com", "password", adminRole);
    User teacher = getOrCreateUser("thomas_teacher", "thomas@school.com", "password", teacherRole);
    User student1 = getOrCreateUser("alice_student", "alice@school.com", "password", studentRole);
    User student2 = getOrCreateUser("bob_student", "bob@school.com", "password", studentRole);
    User student3 =
        getOrCreateUser("charlie_student", "charlie@school.com", "password", studentRole);

    // 3. School Class
    SchoolClass class2026A = getOrCreateClass("Class 2026-A", "Backend development class of 2026");

    // 4. Enrollments
    getOrCreateEnrollment(teacher, class2026A, ClassRole.TEACHER);
    getOrCreateEnrollment(student1, class2026A, ClassRole.STUDENT);
    getOrCreateEnrollment(student2, class2026A, ClassRole.STUDENT);
    getOrCreateEnrollment(student3, class2026A, ClassRole.STUDENT);

    // 5. Course
    Course javaBackend1 =
        getOrCreateCourse(
            "Java Backend 1", "Introduction to Spring Boot and JPA", class2026A, teacher);
    Course testEnd =
        getOrCreateCourse("Testend 2", "A test course to use for testing", class2026A, teacher);

    // 6. Assignments

    Assignment introJava =
        getOrCreateAssignment(
            "Introduction to Java", "Basic syntax and logic", javaBackend1, teacher);
    Assignment springBootLab =
        getOrCreateAssignment("Spring Boot Lab 1", "REST API basics", javaBackend1, teacher);
    Assignment finalProject =
        getOrCreateAssignment(
            "Final Project", "Build a full case management system", javaBackend1, teacher);

    // 7. Student Assignments
    getOrCreateUserAssignment(
        introJava, student1, StudentAssignmentStatus.EVALUATED, "A", "Great work!");
    getOrCreateUserAssignment(introJava, student2, StudentAssignmentStatus.TURNED_IN, null, null);
    getOrCreateUserAssignment(introJava, student3, StudentAssignmentStatus.ASSIGNED, null, null);

    getOrCreateUserAssignment(
        springBootLab, student1, StudentAssignmentStatus.ASSIGNED, null, null);
    getOrCreateUserAssignment(
        springBootLab, student2, StudentAssignmentStatus.ASSIGNED, null, null);

    // 8. Comments
    if (commentRepository.findAll().isEmpty()) {
      Comment comment = new Comment();
      comment.setAssignment(introJava);
      comment.setAuthor(student1);
      comment.setText("I'm having some trouble with the installation, can anyone help?");
      commentRepository.save(comment);
      log.info("Created initial comment for Intro to Java");
    }

    log.info("Data seeding completed successfully.");
  }

  private Role getOrCreateRole(String name) {
    return roleRepository.findByName(name).orElseGet(() -> roleRepository.save(new Role(name)));
  }

  private User getOrCreateUser(String username, String email, String password, Role role) {
    return userRepository
        .findByUsername(username)
        .orElseGet(
            () -> {
              User user = new User();
              user.setUsername(username);
              user.setEmail(email);
              user.setPassword(passwordEncoder.encode(password));
              user.setRole(role);
              return userRepository.save(user);
            });
  }

  private SchoolClass getOrCreateClass(String name, String description) {
    return schoolClassRepository
        .findByName(name)
        .orElseGet(
            () -> {
              SchoolClass sc = new SchoolClass();
              sc.setName(name);
              sc.setDescription(description);
              return schoolClassRepository.save(sc);
            });
  }

  private void getOrCreateEnrollment(User user, SchoolClass schoolClass, ClassRole role) {
    if (classEnrollmentRepository.findByUserAndSchoolClass(user, schoolClass).isEmpty()) {
      ClassEnrollment ce = new ClassEnrollment();
      ce.setUser(user);
      ce.setSchoolClass(schoolClass);
      ce.setClassRole(role);
      classEnrollmentRepository.save(ce);
    }
  }

  private Course getOrCreateCourse(
      String name, String description, SchoolClass schoolClass, User leadTeacher) {
    return courseRepository
        .findByNameAndSchoolClass(name, schoolClass)
        .orElseGet(
            () -> {
              Course course = new Course();
              course.setName(name);
              course.setDescription(description);
              course.setSchoolClass(schoolClass);
              course.setLeadTeacher(leadTeacher);
              return courseRepository.save(course);
            });
  }

  private Assignment getOrCreateAssignment(
      String title, String description, Course course, User creator) {
    // Basic check by title and course
    return assignmentRepository.findAll().stream()
        .filter(a -> a.getTitle().equals(title) && a.getCourse().equals(course))
        .findFirst()
        .orElseGet(
            () -> {
              Assignment a = new Assignment();
              a.setTitle(title);
              a.setDescription(description);
              a.setCourse(course);
              a.setCreator(creator);
              a.setStatus(AssignmentStatus.OPEN);
              return assignmentRepository.save(a);
            });
  }

  private void getOrCreateUserAssignment(
      Assignment assignment,
      User student,
      StudentAssignmentStatus status,
      String grade,
      String feedback) {
    if (userAssignmentRepository.findByAssignmentAndStudent(assignment, student).isEmpty()) {
      UserAssignment ua = new UserAssignment();
      ua.setAssignment(assignment);
      ua.setStudent(student);
      ua.setStatus(status);
      ua.setGrade(grade);
      ua.setFeedback(feedback);
      if (status == StudentAssignmentStatus.TURNED_IN
          || status == StudentAssignmentStatus.EVALUATED) {
        ua.setTurnedInAt(java.time.LocalDateTime.now().minusDays(1));
      }
      userAssignmentRepository.save(ua);
    }
  }
}
