package com.MiniProject.Job.Portal.model.dto;

import lombok.Data;

@Data
public class ApplicationStatusUpdateDto {
    private Long applicationId;
    private String newStatus; // SHORTLISTED or REJECTED

}
