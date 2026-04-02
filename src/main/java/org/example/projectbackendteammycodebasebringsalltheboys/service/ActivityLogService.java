package org.example.projectbackendteammycodebasebringsalltheboys.service;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.ActivityLog;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
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

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(User user, String action, String entityType, UUID entityId, String details) {
        ActivityLog log = new ActivityLog(user, action, entityType, entityId, details);
        activityLogRepository.save(log);
    }

    @Transactional(readOnly = true)
    public Page<ActivityLog> getLogsForUser(User user, Pageable pageable) {
        return activityLogRepository.findByUserOrderByTimestampDesc(user, pageable);
    }

    @Transactional(readOnly = true)
    public Page<ActivityLog> getLogsForEntity(String entityType, UUID entityId, Pageable pageable) {
        return activityLogRepository.findByEntityTypeAndEntityIdOrderByTimestampDesc(
                entityType, entityId, pageable);
    }
}
