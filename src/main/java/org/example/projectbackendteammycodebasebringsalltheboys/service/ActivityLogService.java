package org.example.projectbackendteammycodebasebringsalltheboys.service;

import java.time.Clock;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.ActivityLog;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Assignment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.ActivityAction;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.ActivityStatus;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.EntityType;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.ActivityLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ActivityLogService {

  private final ActivityLogRepository activityLogRepository;
  private final Clock clock;

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void log(
      User user,
      Long caseID,
      ActivityAction action,
      EntityType entityType,
      Long entityId,
      Map<String, Object> details,
      ActivityStatus status) {
    ActivityLog log =
        new ActivityLog(user, caseID, action, entityType, entityId, details, status, clock);
    activityLogRepository.save(log);
  }

  @Transactional(readOnly = true)
  public Page<ActivityLog> getLogsForUser(User user, Pageable pageable) {
    return activityLogRepository.findByUserOrderByTimestampDescIdDesc(user, pageable);
  }

  @Transactional(readOnly = true)
  public Page<ActivityLog> getLogsForCase(Long caseId, Pageable pageable) {
    return activityLogRepository.findByCaseIdOrderByTimestampDescIdDesc(caseId, pageable);
  }

  @Transactional(readOnly = true)
  public Page<ActivityLog> getLogsForAssignment(Assignment assignment, Pageable pageable) {
    return getLogsForCase(assignment.getId(), pageable);
  }

  @Transactional(readOnly = true)
  public Page<ActivityLog> getLogsForEntity(
      EntityType entityType, Long entityId, Pageable pageable) {
    return activityLogRepository.findByEntityTypeAndEntityIdOrderByTimestampDescIdDesc(
        entityType, entityId, pageable);
  }
}
