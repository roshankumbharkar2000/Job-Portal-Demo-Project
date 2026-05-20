package com.MiniProject.Job.Portal.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobEventDto implements Serializable {
    private Long jobId;
    private String jobTitle;
    private String employerName;
    private String candidateName;
    private LocalDateTime appliedAt;

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public void setEmployerName(String employerName) {
        this.employerName = employerName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public void setAppliedAt(LocalDateTime appliedAt) {
        this.appliedAt = appliedAt;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public String getEmployerName() {
        return employerName;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public LocalDateTime getAppliedAt() {
        return appliedAt;
    }
}
