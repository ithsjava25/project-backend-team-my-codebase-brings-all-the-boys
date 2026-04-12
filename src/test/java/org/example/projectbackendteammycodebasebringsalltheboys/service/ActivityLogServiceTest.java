package org.example.projectbackendteammycodebasebringsalltheboys.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.UUID;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.ActivityLog;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.ActivityAction;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.ActivityStatus;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.EntityType;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.ActivityLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ActivityLogServiceTest {

  @Mock private ActivityLogRepository activityLogRepository;
  private Clock clock = Clock.fixed(Instant.parse("2026-04-12T10:00:00Z"), ZoneId.of("UTC"));

  private ActivityLogService activityLogService;

  @BeforeEach
  void setUp() {
    activityLogService = new ActivityLogService(activityLogRepository, clock);
  }

  @Test
  @DisplayName("log creates and saves ActivityLog with correct details")
  void log_savesActivityLog() {
    User user = new User();
    user.setId(UUID.randomUUID());
    UUID parentId = UUID.randomUUID();
    UUID entityId = UUID.randomUUID();
    Map<String, Object> details = Map.of("key", "value");

    activityLogService.log(user, parentId, ActivityAction.CREATED, EntityType.COURSE, entityId, details, ActivityStatus.SUCCESS);

    ArgumentCaptor<ActivityLog> captor = ArgumentCaptor.forClass(ActivityLog.class);
    verify(activityLogRepository).save(captor.capture());

    ActivityLog saved = captor.getValue();
    assertThat(saved.getUser()).isEqualTo(user);
    assertThat(saved.getParentId()).isEqualTo(parentId);
    assertThat(saved.getAction()).isEqualTo(ActivityAction.CREATED);
    assertThat(saved.getEntityType()).isEqualTo(EntityType.COURSE);
    assertThat(saved.getChildId()).isEqualTo(entityId);
    assertThat(saved.getDetails()).isEqualTo(details);
    assertThat(saved.getStatus()).isEqualTo(ActivityStatus.SUCCESS);
    assertThat(saved.getTimestamp()).isEqualTo(LocalDateTime.now(clock));
  }
}
