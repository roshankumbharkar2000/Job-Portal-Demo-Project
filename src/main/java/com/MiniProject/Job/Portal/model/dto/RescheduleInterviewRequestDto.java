package com.MiniProject.Job.Portal.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RescheduleInterviewRequestDto {
    private Long interviewId;
    private LocalDateTime newScheduledAt;
}
