package com.MiniProject.Job.Portal.services;

import com.MiniProject.Job.Portal.model.dto.*;
import com.MiniProject.Job.Portal.model.entity.JobApplication;

import java.util.List;

public interface JobApplicationService  {
    JobApplication applyForJob(JobApplicationRequestDto request);

    List<AppliedJobResponseDto> getAppliedJobsForCandidate();

    List<JobApplicationForEmployerDto> getApplicationsForEmployer(Long employerId);
    String updateApplicationStatus(Long employerId, ApplicationStatusUpdateDto statusUpdateDto);
    String updateInterviewStatus(Long employerId, InterviewStatusUpdateDto statusUpdateDto);

    List<InterviewDetailsDto> getAcceptedInterviewsByJobId(Long jobId);
}