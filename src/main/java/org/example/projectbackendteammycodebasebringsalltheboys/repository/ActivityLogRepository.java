package org.example.projectbackendteammycodebasebringsalltheboys.repository;

import java.util.UUID;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.ActivityLog;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.EntityType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, UUID> {
  Page<ActivityLog> findByUserOrderByTimestampDescIdDesc(User user, Pageable pageable);

  Page<ActivityLog> findByEntityTypeAndParentIdOrderByTimestampDescIdDesc(
      EntityType entityType, UUID parentId, Pageable pageable);

  Page<ActivityLog> findByParentIdOrderByTimestampDescIdDesc(UUID caseId, Pageable pageable);
}
