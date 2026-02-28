package com.blog.demo.reports;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
//DTO : Data Transfer Object
public class ReportRequest {

    private Long reportedUserId;
    private Long reportedPostId;
    @NotBlank(message = "reason is required")
    // @Size(max = 50, message = "can't exceed 50 chars")
    @Size(min = 2,max = 50, message = "reason must be btwween 2 and 50 chars")
    private String reason;
}
