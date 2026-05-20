package com.MiniProject.Job.Portal.model.dto;

import lombok.Data;

@Data
public class JobApplicationForEmployerDto {
    private Long applicationId;
    private Long candidateId;
    private String candidateName;
    private String candidateEmail;
    private String jobTitle;
    private String applicationStatus;
    private String resumeLink; // If you're storing resume URLs
}
