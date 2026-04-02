package org.example.projectbackendteammycodebasebringsalltheboys.repository;

import org.example.projectbackendteammycodebasebringsalltheboys.entity.ActivityLog;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.EntityType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
  Page<ActivityLog> findByUserOrderByTimestampDescIdDesc(User user, Pageable pageable);

  Page<ActivityLog> findByEntityTypeAndEntityIdOrderByTimestampDescIdDesc(
      EntityType entityType, Long entityId, Pageable pageable);

  Page<ActivityLog> findByCaseIdOrderByTimestampDescIdDesc(Long caseId, Pageable pageable);
}
