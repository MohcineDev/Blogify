package com.blog.demo.dashboard;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardReportDto {

    private Long reportId;
    private String reason;
    private String reportedUsername;
    private Long reportedUser;
    private Long reportedPost;
    private LocalDateTime timestamp;
 

}
