package org.example.projectbackendteammycodebasebringsalltheboys.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.ActivityLog;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Assignment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.ActivityAction;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.EntityType;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.ActivityLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ActivityLogService {

  private final ActivityLogRepository activityLogRepository;

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void log(User user, Long caseId, ActivityAction action, EntityType entityType, Long entityId, String details) {
    ActivityLog log = new ActivityLog(user, caseId, action, entityType, entityId, details);
    activityLogRepository.save(log);
  }

  @Transactional(readOnly = true)
  public List<ActivityLog> getLogsForUser(User user) {
    return activityLogRepository.findByUserOrderByTimestampDesc(user);
  }

  @Transactional(readOnly = true)
  public List<ActivityLog> getLogsForCase(Long caseId) {
    return activityLogRepository.findByCaseIdOrderByTimestampDesc(caseId);
  }

  @Transactional(readOnly = true)
  public List<ActivityLog> getLogsForAssignment(Assignment assignment) {
    return getLogsForCase(assignment.getId());
  }

  @Transactional(readOnly = true)
  public List<ActivityLog> getLogsForEntity(String entityType, Long entityId) {
    return activityLogRepository.findByEntityTypeAndEntityIdOrderByTimestampDesc(
        entityType, entityId);
  }
}

