package com.MiniProject.Job.Portal.model.dto;


import com.MiniProject.Job.Portal.model.entity.InterviewStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InterviewResponseDto {
    private Long interviewId;
    private Long candidateId;
    private String candidateEmail;
    private LocalDateTime scheduledAt;
    private String mode;
    private String location;
    private InterviewStatus status;
}
