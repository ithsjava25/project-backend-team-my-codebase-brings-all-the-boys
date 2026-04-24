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

    // 2. Admin
    User admin = getOrCreateUser("admin", "admin@school.com", "password", adminRole);

    // 3. Teachers
    User teacher1 = getOrCreateUser("thomas_teacher", "thomas@school.com", "password", teacherRole);
    User teacher2 = getOrCreateUser("emma_teacher", "emma@school.com", "password", teacherRole);
    User teacher3 = getOrCreateUser("johan_teacher", "johan@school.com", "password", teacherRole);
    User teacher4 = getOrCreateUser("sara_teacher", "sara@school.com", "password", teacherRole);
    User teacher5 = getOrCreateUser("lina_teacher", "lina@school.com", "password", teacherRole);

    // 4. Classes
    SchoolClass classA = getOrCreateClass("Class 2026-A", "Backend development");
    SchoolClass classB = getOrCreateClass("Class 2026-B", "Frontend development");
    SchoolClass classC = getOrCreateClass("Class 2026-C", "Fullstack development");

    // 5. Students
    User s1 = getOrCreateUser("alice", "alice@school.com", "password", studentRole);
    User s2 = getOrCreateUser("bob", "bob@school.com", "password", studentRole);
    User s3 = getOrCreateUser("charlie", "charlie@school.com", "password", studentRole);
    User s4 = getOrCreateUser("david", "david@school.com", "password", studentRole);
    User s5 = getOrCreateUser("ella", "ella@school.com", "password", studentRole);

    User s6 = getOrCreateUser("felix", "felix@school.com", "password", studentRole);
    User s7 = getOrCreateUser("greta", "greta@school.com", "password", studentRole);
    User s8 = getOrCreateUser("henrik", "henrik@school.com", "password", studentRole);
    User s9 = getOrCreateUser("ida", "ida@school.com", "password", studentRole);
    User s10 = getOrCreateUser("jack", "jack@school.com", "password", studentRole);

    User s11 = getOrCreateUser("klara", "klara@school.com", "password", studentRole);
    User s12 = getOrCreateUser("leo", "leo@school.com", "password", studentRole);
    User s13 = getOrCreateUser("maria", "maria@school.com", "password", studentRole);
    User s14 = getOrCreateUser("noah", "noah@school.com", "password", studentRole);
    User s15 = getOrCreateUser("olivia", "olivia@school.com", "password", studentRole);

    // 6. Enrollments
    // Class A
    getOrCreateEnrollment(teacher1, classA, ClassRole.TEACHER);
    getOrCreateEnrollment(s1, classA, ClassRole.STUDENT);
    getOrCreateEnrollment(s2, classA, ClassRole.STUDENT);
    getOrCreateEnrollment(s3, classA, ClassRole.STUDENT);
    getOrCreateEnrollment(s4, classA, ClassRole.STUDENT);
    getOrCreateEnrollment(s5, classA, ClassRole.STUDENT);

    // Class B
    getOrCreateEnrollment(teacher2, classB, ClassRole.TEACHER);
    getOrCreateEnrollment(s6, classB, ClassRole.STUDENT);
    getOrCreateEnrollment(s7, classB, ClassRole.STUDENT);
    getOrCreateEnrollment(s8, classB, ClassRole.STUDENT);
    getOrCreateEnrollment(s9, classB, ClassRole.STUDENT);
    getOrCreateEnrollment(s10, classB, ClassRole.STUDENT);

    // Class C
    getOrCreateEnrollment(teacher3, classC, ClassRole.TEACHER);
    getOrCreateEnrollment(s11, classC, ClassRole.STUDENT);
    getOrCreateEnrollment(s12, classC, ClassRole.STUDENT);
    getOrCreateEnrollment(s13, classC, ClassRole.STUDENT);
    getOrCreateEnrollment(s14, classC, ClassRole.STUDENT);
    getOrCreateEnrollment(s15, classC, ClassRole.STUDENT);

    // 7. Courses - Focused on specific classes
    Course backend =
        getOrCreateCourse(
            "Java Backend",
            "Avancerad backend-utveckling med Spring Boot, JPA och säkerhet. Fokus på att bygga skalbara och robusta system.",
            classA,
            teacher1);
    Course databases =
        getOrCreateCourse(
            "Databases",
            "Lär dig designa, implementera och optimera relationella databaser med PostgreSQL. Inkluderar avancerad SQL och prestandaoptimering.",
            classA,
            teacher3);

    Course frontend =
        getOrCreateCourse(
            "React Frontend",
            "Modern webbutveckling med React, TypeScript och Tailwind CSS. Vi bygger interaktiva och responsiva användargränssnitt.",
            classB,
            teacher2);
    Course cloud =
        getOrCreateCourse(
            "Cloud Basics",
            "Introduktion till molnbaserad infrastruktur (AWS/Azure). Fokus på deployment, skalning och serverlös arkitektur.",
            classB,
            teacher4);

    Course devops =
        getOrCreateCourse(
            "DevOps",
            "CI/CD, Docker och Kubernetes. Vi automatiserar hela utvecklingscykeln från kod till produktion.",
            classC,
            teacher4);
    Course testing =
        getOrCreateCourse(
            "Testing",
            "Kvalitetssäkring genom enhetstester, integrationstester och E2E-tester med JUnit och Vitest.",
            classC,
            teacher5);

    // Add some assistants
    backend.getAssistants().add(teacher2);
    frontend.getAssistants().add(teacher1);
    databases.getAssistants().add(teacher5);
    courseRepository.saveAll(java.util.List.of(backend, frontend, databases));

    // 8. Assignments
    Assignment a1 =
        getOrCreateAssignment(
            "Intro Java",
            "Lär dig grunderna i Java. Installera JDK, sätt upp Maven och skriv ditt första 'Hello World'-program. Fokus på variabler, datatyper och kontrollflöden.",
            backend,
            teacher1);

    Assignment a2 =
        getOrCreateAssignment(
            "Build REST API",
            "Skapa en komplett REST-backend med Spring Boot. Implementera controllers, tjänster och repositories för att hantera ärenden i ett skolsystem. Inkludera felhantering och DTO-mappning.",
            backend,
            teacher1);

    Assignment a3 =
        getOrCreateAssignment(
            "React Components",
            "Designa och bygg ett bibliotek av återanvändbara UI-komponenter med React och Tailwind CSS. Fokus på props, state och tillgänglighet (accessibility).",
            frontend,
            teacher2);

    Assignment a4 =
        getOrCreateAssignment(
            "SQL Queries",
            "Designa ett databasschema och skriv komplexa SQL-frågor. Använd JOINs, aggregatfunktioner (GROUP BY) och subqueries för att extrahera statistik från systemet.",
            databases,
            teacher3);

    Assignment a5 =
        getOrCreateAssignment(
            "Unit Testing",
            "Skriv robusta enhetstester med JUnit 5 och Mockito. Målet är 80% kodtäckning på dina service-klasser.",
            testing,
            teacher5);

    Assignment a6 =
        getOrCreateAssignment(
            "Dockerize App",
            "Skapa en Dockerfile för din Spring Boot-applikation och sätt upp en docker-compose för att köra appen tillsammans med en PostgreSQL-databas.",
            devops,
            teacher4);

    // 9. Student assignments - varied examples for demo
    // Class A
    getOrCreateUserAssignment(
        a1,
        s1,
        StudentAssignmentStatus.EVALUATED,
        "A",
        "Utmärkt arbete! Din kod är ren och välstrukturerad.");
    getOrCreateUserAssignment(a2, s1, StudentAssignmentStatus.TURNED_IN, null, null);
    getOrCreateUserAssignment(a4, s1, StudentAssignmentStatus.ASSIGNED, null, null);

    getOrCreateUserAssignment(
        a1,
        s2,
        StudentAssignmentStatus.EVALUATED,
        "C",
        "Bra start, men tänk på namngivning av variabler.");
    getOrCreateUserAssignment(a4, s2, StudentAssignmentStatus.TURNED_IN, null, null);

    // Class B
    getOrCreateUserAssignment(a3, s6, StudentAssignmentStatus.ASSIGNED, null, null);
    getOrCreateUserAssignment(a3, s7, StudentAssignmentStatus.TURNED_IN, null, null);

    // Class C
    getOrCreateUserAssignment(
        a5,
        s11,
        StudentAssignmentStatus.EVALUATED,
        "B",
        "Mycket bra tester, du har täckt de flesta edge cases.");
    getOrCreateUserAssignment(a6, s11, StudentAssignmentStatus.ASSIGNED, null, null);

    // 10. Comments - adding some life to the discussions
    getOrCreateComment(
        "Kom ihåg att kolla på de nya DTO-mappningarna vi gick igenom igår!", a2, teacher1, null);
    getOrCreateComment(
        "Jag har laddat upp en exempelfil för SQL-uppgiften nu.", a4, teacher3, null);

    // Student question on an assignment
    getOrCreateComment("Hur hanterar vi validering av email-formatet bäst?", a2, s1, null);

    log.info("Data seeding completed successfully with enriched data and comments.");
  }

  private void getOrCreateComment(
      String text, Assignment assignment, User author, UserAssignment ua) {
    if (!commentRepository.existsByTextAndAssignmentAndAuthor(text, assignment, author)) {
      Comment comment = new Comment();
      comment.setText(text);
      comment.setAuthor(author);
      comment.setAssignment(assignment);
      comment.setUserAssignment(ua);
      commentRepository.save(comment);
    }
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
              course.setEndDate(java.time.LocalDateTime.now().plusYears(1));
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
              // Add a deadline 1-2 weeks from now
              a.setDeadline(java.time.LocalDateTime.now().plusDays(7 + (long) (Math.random() * 7)));
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
