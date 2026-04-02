package org.example.projectbackendteammycodebasebringsalltheboys.repository;

import java.util.List;
import java.util.UUID;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.ActivityLog;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, UUID> {
  List<ActivityLog> findByUserOrderByTimestampDesc(User user);

  List<ActivityLog> findByEntityTypeAndEntityIdOrderByTimestampDesc(
      String entityType, UUID entityId);
}
