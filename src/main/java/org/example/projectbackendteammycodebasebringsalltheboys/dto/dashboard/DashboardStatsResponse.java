package org.example.projectbackendteammycodebasebringsalltheboys.dto.dashboard;

import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardStatsResponse {
  private Map<String, Object> stats;
}
