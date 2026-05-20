package com.MiniProject.Job.Portal.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InterviewScheduleRequestDto {
    private Long applicationId;
    private LocalDateTime scheduledAt;
    private String mode; // e.g., Zoom, Onsite
    private String location;
    private String note;
}
