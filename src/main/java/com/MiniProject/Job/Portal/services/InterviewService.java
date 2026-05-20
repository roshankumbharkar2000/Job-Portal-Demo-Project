package com.MiniProject.Job.Portal.services;

import com.MiniProject.Job.Portal.model.dto.InterviewResponseDto;
import com.MiniProject.Job.Portal.model.dto.InterviewScheduleRequestDto;
import com.MiniProject.Job.Portal.model.dto.RescheduleInterviewRequestDto;

import java.util.List;

public interface InterviewService {
    String scheduleInterview(Long employerId, InterviewScheduleRequestDto request);

    List<InterviewResponseDto> getInterviewsByJobId(Long jobId, Long employerId);

    String rescheduleInterviewByEmployer(RescheduleInterviewRequestDto request, Long employerId);

    String requestRescheduleByCandidate(RescheduleInterviewRequestDto request, Long candidateId);

    String confirmInterview(Long interviewId, Long candidateId);

    abstract List<InterviewResponseDto> getInterviewsForCandidate();
}
