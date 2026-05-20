package com.MiniProject.Job.Portal.model.dto;


import com.MiniProject.Job.Portal.model.entity.ApplicationStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppliedJobResponseDto {
    private Long jobId;
    private String title;
    private String location;
    private String engagementType;
    private String jobType;
    private ApplicationStatus status;
    private LocalDateTime appliedAt;
}