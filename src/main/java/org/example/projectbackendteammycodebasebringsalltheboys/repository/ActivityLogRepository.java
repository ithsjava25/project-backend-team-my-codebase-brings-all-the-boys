package org.example.projectbackendteammycodebasebringsalltheboys.repository;

import org.example.projectbackendteammycodebasebringsalltheboys.entity.ActivityLog;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    List<ActivityLog> findByUserOrderByTimestampDesc(User user);
    List<ActivityLog> findByEntityTypeAndEntityIdOrderByTimestampDesc(String entityType, Long entityId);
}
