package com.MiniProject.Job.Portal.model.dto;

import lombok.Data;

@Data
public class JobApplicationRequestDto {
    private Long jobId;

    public JobApplicationRequestDto() {
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }
}
