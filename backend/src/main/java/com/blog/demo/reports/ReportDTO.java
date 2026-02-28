package com.blog.demo.reports;

import java.time.LocalDateTime;

public record ReportDTO(
    Long id,
    String reason,
    String status,
    LocalDateTime timestamp,
    String reporterUsername,
    Long reportedPostId,
    String ReportedUsername,
    Long ReportedUserId,
    boolean isBanned,
    boolean hided
) {
    
}
