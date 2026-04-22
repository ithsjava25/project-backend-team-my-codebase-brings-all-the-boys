package org.example.projectbackendteammycodebasebringsalltheboys.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.dashboard.DashboardStatsResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.dashboard.PendingSubmissionDTO;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.dashboard.UpcomingDeadlineDTO;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Assignment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.StudentAssignmentStatus;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

  private final UserRepository userRepository;
  private final CourseRepository courseRepository;
  private final AssignmentRepository assignmentRepository;
  private final UserAssignmentRepository userAssignmentRepository;

  public DashboardStatsResponse getStats(User user) {
    Map<String, Object> stats = new HashMap<>();
    String role = user.getRole().getName();

    if ("ROLE_ADMIN".equals(role)) {
      stats.put("totalUsers", userRepository.count());
      stats.put("totalStudents", userRepository.countByRole_Name("ROLE_STUDENT"));
      stats.put("totalTeachers", userRepository.countByRole_Name("ROLE_TEACHER"));
      stats.put("totalCourses", courseRepository.count());
    } else if ("ROLE_TEACHER".equals(role)) {
      stats.put(
          "pendingGrading",
          userAssignmentRepository.countByStatusAndAssignment_Course_LeadTeacher_Id(
              StudentAssignmentStatus.TURNED_IN, user.getId()));
      stats.put(
          "activeCourses",
          courseRepository
              .findByLeadTeacherId(user.getId(), org.springframework.data.domain.Pageable.unpaged())
              .getTotalElements());
    } else if ("ROLE_STUDENT".equals(role)) {
      stats.put(
          "pendingAssignments",
          userAssignmentRepository.countByStudent_IdAndStatus(
              user.getId(), StudentAssignmentStatus.ASSIGNED));

      LocalDateTime now = LocalDateTime.now();
      LocalDateTime weekFromNow = now.plusDays(7);
      Instant weekFromNowInstant = weekFromNow.atZone(ZoneId.systemDefault()).toInstant();
      List<UpcomingDeadlineDTO> upcoming =
          getUpcomingDeadlines(user).stream()
              .sorted(Comparator.comparing(UpcomingDeadlineDTO::getDeadline))
              .collect(Collectors.toList());
      long weekCount =
          upcoming.stream().filter(d -> d.getDeadline().isBefore(weekFromNowInstant)).count();
      stats.put("upcomingDeadlinesCount", weekCount);
      upcoming.stream().findFirst().ifPresent(d -> stats.put("nextDeadline", d.getDeadline()));
    }

    return DashboardStatsResponse.builder().stats(stats).build();
  }

  public List<PendingSubmissionDTO> getPendingSubmissions(User teacher) {
    return userAssignmentRepository
        .findByStatusAndAssignment_Course_LeadTeacher_Id(
            StudentAssignmentStatus.TURNED_IN, teacher.getId())
        .stream()
        .map(
            ua ->
                PendingSubmissionDTO.builder()
                    .userAssignmentId(ua.getId())
                    .assignmentId(ua.getAssignment().getId())
                    .assignmentTitle(ua.getAssignment().getTitle())
                    .studentName(ua.getStudent().getUsername())
                    .submittedAt(
                        ua.getTurnedInAt() != null
                            ? ua.getTurnedInAt().atZone(ZoneId.systemDefault()).toInstant()
                            : null)
                    .courseId(ua.getAssignment().getCourse().getId())
                    .courseName(ua.getAssignment().getCourse().getName())
                    .build())
        .collect(Collectors.toList());
  }

  public List<UpcomingDeadlineDTO> getUpcomingDeadlines(User user) {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime end = now.plusDays(14); // Next 2 weeks
    String role = user.getRole().getName();

    if ("ROLE_TEACHER".equals(role)) {
      return assignmentRepository
          .findByCourse_LeadTeacher_IdAndDeadlineBetween(user.getId(), now, end)
          .stream()
          .map(
              a ->
                  UpcomingDeadlineDTO.builder()
                      .assignmentId(a.getId())
                      .title(a.getTitle())
                      .deadline(a.getDeadline().atZone(ZoneId.systemDefault()).toInstant())
                      .courseName(a.getCourse().getName())
                      .build())
          .collect(Collectors.toList());
    } else {
      List<Assignment> assignments =
          assignmentRepository.findByCourse_SchoolClass_Enrollments_User_IdAndDeadlineBetween(
              user.getId(), now, end);
      Map<UUID, String> statusByAssignmentId =
          userAssignmentRepository.findByStudentAndAssignmentIn(user, assignments).stream()
              .collect(
                  Collectors.toMap(ua -> ua.getAssignment().getId(), ua -> ua.getStatus().name()));
      return assignments.stream()
          .map(
              a ->
                  UpcomingDeadlineDTO.builder()
                      .assignmentId(a.getId())
                      .title(a.getTitle())
                      .deadline(a.getDeadline().atZone(ZoneId.systemDefault()).toInstant())
                      .courseName(a.getCourse().getName())
                      .status(statusByAssignmentId.getOrDefault(a.getId(), "NOT_ASSIGNED"))
                      .build())
          .collect(Collectors.toList());
    }
  }
}
