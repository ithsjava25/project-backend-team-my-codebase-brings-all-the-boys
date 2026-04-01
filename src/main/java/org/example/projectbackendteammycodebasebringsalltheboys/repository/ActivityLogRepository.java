package org.example.projectbackendteammycodebasebringsalltheboys.repository;

import java.util.List;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.ActivityLog;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
  Page<ActivityLog> findByUserOrderByTimestampDesc(User user, Pageable pageable);

  Page<ActivityLog> findByEntityTypeAndEntityIdOrderByTimestampDesc(
      String entityType, Long entityId, Pageable pageable);

  Page<ActivityLog> findByCaseIdOrderByTimestampDesc(Long caseId, Pageable pageable);
}
