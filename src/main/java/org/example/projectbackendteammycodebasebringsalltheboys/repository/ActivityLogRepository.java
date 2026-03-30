package org.example.projectbackendteammycodebasebringsalltheboys.repository;

import java.util.List;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.ActivityLog;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
  List<ActivityLog> findByUserOrderByTimestampDesc(User user);

  List<ActivityLog> findByEntityTypeAndEntityIdOrderByTimestampDesc(
      String entityType, Long entityId);
}
