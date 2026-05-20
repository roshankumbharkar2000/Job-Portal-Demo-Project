package com.MiniProject.Job.Portal.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InterviewDetailsDto {
    private Long candidateId;
    private String candidateEmail;
    private String jobTitle;
    private LocalDateTime appliedAt;
}
