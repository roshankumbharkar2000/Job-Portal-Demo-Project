package com.MiniProject.Job.Portal.services.ServiceImplementastion;

import com.MiniProject.Job.Portal.model.dto.InterviewResponseDto;
import com.MiniProject.Job.Portal.model.dto.InterviewScheduleRequestDto;
import com.MiniProject.Job.Portal.model.dto.RescheduleInterviewRequestDto;
import com.MiniProject.Job.Portal.model.entity.*;
import com.MiniProject.Job.Portal.repository.CandidateProfileRepository;
import com.MiniProject.Job.Portal.repository.InterviewRepository;
import com.MiniProject.Job.Portal.repository.JobApplicationRepository;
import com.MiniProject.Job.Portal.repository.JobPostRepository;
import com.MiniProject.Job.Portal.services.InterviewService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InterviewServiceImpl implements InterviewService {

    private final JobApplicationRepository jobApplicationRepository;
    private final InterviewRepository interviewRepository;
    private final JobPostRepository jobPostRepository;
    private final UserServiceImpl userService;
    private final CandidateProfileRepository candidateProfileRepository;

    public InterviewServiceImpl(JobApplicationRepository jobApplicationRepository,
                                InterviewRepository interviewRepository, JobPostRepository jobPostRepository, UserServiceImpl userService, CandidateProfileRepository candidateProfileRepository) {
        this.jobApplicationRepository = jobApplicationRepository;
        this.interviewRepository = interviewRepository;
        this.jobPostRepository = jobPostRepository;
        this.userService = userService;
        this.candidateProfileRepository = candidateProfileRepository;
    }
    @Override
    public String scheduleInterview(Long employerId, InterviewScheduleRequestDto request) {
        JobApplication application = jobApplicationRepository.findById(request.getApplicationId())
                .orElseThrow(() -> new RuntimeException("Job application not found"));

        // Make sure employer owns the job
        if (!application.getJob().getUserId().equals(employerId)) {
            throw new RuntimeException("You are not authorized to schedule interview for this application");
        }

        // Only schedule if status is SHORTLISTED
        if (!application.getStatus().toString().equalsIgnoreCase("SHORTLISTED")) {
            throw new RuntimeException("Interview can only be scheduled for SHORTLISTED candidates.");
        }

        // Check if already scheduled
        if (interviewRepository.findByJobApplication(application).isPresent()) {
            throw new RuntimeException("Interview already scheduled for this application.");
        }

        Interview interview = Interview.builder()
                .jobApplication(application)
                .scheduledAt(request.getScheduledAt())
                .mode(request.getMode())
                .locationorLink(request.getLocation())
                .status(InterviewStatus.SCHEDULED)
                .note(request.getNote())
                .build();

        interviewRepository.save(interview);

        return "Interview scheduled successfully!";
    }


    //Get Interviews by Job ID
    @Override
    public List<InterviewResponseDto> getInterviewsByJobId(Long jobId, Long employerId) {
        JobPost job = jobPostRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (!job.getUserId().equals(employerId)) {
            throw new RuntimeException("You are not authorized to view interviews for this job");
        }

        List<JobApplication> applications = jobApplicationRepository.findByJob(job);

        List<Interview> interviews = interviewRepository.findAllByJobApplicationIn(applications);

        return interviews.stream().map(interview -> {
            InterviewResponseDto dto = new InterviewResponseDto();
            dto.setInterviewId(interview.getId());
            dto.setCandidateId(interview.getJobApplication().getCandidate().getId());
            dto.setCandidateEmail(interview.getJobApplication().getCandidate().getUser().getEmail());
            dto.setScheduledAt(interview.getScheduledAt());
            dto.setMode(interview.getMode());
            dto.setLocation(interview.getLocationorLink());
            dto.setStatus(interview.getStatus());
            return dto;
        }).toList();
    }


    @Override
    public String rescheduleInterviewByEmployer(RescheduleInterviewRequestDto request, Long employerId) {
        Interview interview = interviewRepository.findById(request.getInterviewId())
                .orElseThrow(() -> new RuntimeException("Interview not found"));

        JobApplication application = interview.getJobApplication();
        JobPost job = application.getJob();

        if (!job.getUserId().equals(employerId)) {
            throw new RuntimeException("You are not authorized to reschedule this interview");
        }

        interview.setScheduledAt(request.getNewScheduledAt());
        interview.setStatus(InterviewStatus.RESCHEDULED);

        interviewRepository.save(interview);

        return "Interview rescheduled successfully.";
    }


    @Override
    public String requestRescheduleByCandidate(RescheduleInterviewRequestDto request, Long candidateId) {
        Interview interview = interviewRepository.findById(request.getInterviewId())
                .orElseThrow(() -> new RuntimeException("Interview not found"));

        JobApplication application = interview.getJobApplication();
        if (!application.getCandidate().getUser().getUserId().equals(candidateId)) {
            throw new RuntimeException("Unauthorized reschedule request");
        }

        interview.setReScheduledAt(request.getNewScheduledAt());
        interview.setStatus(InterviewStatus.REQUEST_FOR_RESCHEDULE);

        interviewRepository.save(interview);
        return "Reschedule request submitted to employer.";
    }

    //Confirm Interview
    @Override
    public String confirmInterview(Long interviewId, Long candidateId) {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new RuntimeException("Interview not found"));

        JobApplication application = interview.getJobApplication();
        if (!application.getCandidate().getUser().getUserId().equals(candidateId)) {
            throw new RuntimeException("You are not authorized to confirm this interview");
        }

        if (interview.getStatus() != InterviewStatus.SCHEDULED && interview.getStatus() != InterviewStatus.RESCHEDULED) {
            throw new RuntimeException("Interview is not in a state that can be confirmed");
        }

        interview.setStatus(InterviewStatus.CONFIRMED);
        interviewRepository.save(interview);
        return "Interview confirmed successfully.";
    }

    @Override
    public List<InterviewResponseDto> getInterviewsForCandidate() {
        User user = userService.getLoggedInUser();

        if (!user.getRole().equals(Role.CANDIDATE)) {
            throw new RuntimeException("Only candidates can view their interviews");
        }

        Candidate candidate = candidateProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Candidate profile not found"));

        List<Interview> interviews = interviewRepository.findByJobApplication_Candidate(candidate);

        return interviews.stream().map(this::convertToResponseDto).collect(Collectors.toList());
    }

    private InterviewResponseDto convertToResponseDto(Interview interview) {
        InterviewResponseDto dto = new InterviewResponseDto();
        dto.setInterviewId(interview.getId());
        dto.setInterviewId(interview.getJobApplication().getId());
        dto.setScheduledAt(interview.getScheduledAt());
        dto.setLocation(interview.getLocationorLink());
        dto.setStatus(interview.getStatus());
        return dto;
    }





}
