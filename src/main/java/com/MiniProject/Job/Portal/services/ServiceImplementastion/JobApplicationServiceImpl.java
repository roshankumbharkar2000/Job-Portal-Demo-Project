package com.MiniProject.Job.Portal.services.ServiceImplementastion;


import com.MiniProject.Job.Portal.model.dto.*;
import com.MiniProject.Job.Portal.model.entity.*;
import com.MiniProject.Job.Portal.repository.CandidateProfileRepository;
import com.MiniProject.Job.Portal.repository.JobApplicationRepository;
import com.MiniProject.Job.Portal.repository.JobPostRepository;
import com.MiniProject.Job.Portal.services.JobApplicationService;
import com.MiniProject.Job.Portal.services.MailService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class JobApplicationServiceImpl implements JobApplicationService {

    private final JobPostRepository jobRepository;
    private final CandidateProfileRepository candidateProfileRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final UserServiceImpl userService;
    private final MailService mailService;
    private final KafkaProducerService kafkaProducerService;

    public JobApplicationServiceImpl(JobPostRepository jobRepository, CandidateProfileRepository candidateProfileRepository, JobApplicationRepository jobApplicationRepository, UserServiceImpl userService, MailService mailService, KafkaProducerService kafkaProducerService) {
        this.jobRepository = jobRepository;
        this.candidateProfileRepository = candidateProfileRepository;
        this.jobApplicationRepository = jobApplicationRepository;
        this.userService = userService;
        this.mailService = mailService;

        this.kafkaProducerService = kafkaProducerService;
    }


    @Override
    @Transactional
    public JobApplication applyForJob(JobApplicationRequestDto request) {
        User user = userService.getLoggedInUser();
        if (!user.getRole().equals(Role.CANDIDATE)) {
            throw new RuntimeException("Only candidates can apply for jobs.");
        }
        Candidate candidate = candidateProfileRepository.findByUser(user).orElseThrow(()-> new RuntimeException("candidate not found"));
        JobPost job = jobRepository.findById(request.getJobId())
                .orElseThrow(() -> new RuntimeException("Job not found"));

        jobApplicationRepository.findByCandidateAndJob(candidate, job)
                .ifPresent(app -> {
                    throw new RuntimeException("You have already applied for this job.");
                });

        JobApplication application = new JobApplication();
        application.setCandidate(candidate);
        application.setJob(job);
        application.setAppliedAt(LocalDateTime.now());
        application.setStatus(ApplicationStatus.APPLIED);


        JobApplication savedApplication = jobApplicationRepository.save(application);
        System.out.println("candidate save:"+savedApplication);

//        // Send application confirmation email to candidate
//        sendApplicationConfirmationEmail(candidate, job);

        // Send Kafka message
        String message = candidate.getUser().getFirstName() + " applied for job ID: " + job;
        kafkaProducerService.sendCandidateAppliedEvent(message);

        return savedApplication;

    }
    @Override
    public List<AppliedJobResponseDto> getAppliedJobsForCandidate() {
        User user = userService.getLoggedInUser();
        if (!user.getRole().equals(Role.CANDIDATE)) {
            throw new RuntimeException("Only candidates can view applied jobs.");
        }

        Candidate candidate = candidateProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Candidate profile not found"));

        List<JobApplication> applications = jobApplicationRepository.findByCandidate(candidate);

        return applications.stream().map(app -> {
            AppliedJobResponseDto dto = new AppliedJobResponseDto();
            JobPost job = app.getJob();
            dto.setJobId(job.getId());
            dto.setTitle(job.getTitle());
            dto.setLocation(job.getLocation());
            dto.setEngagementType(job.getEngagementType());
            dto.setJobType(job.getJobType());
            dto.setStatus(app.getStatus());
            dto.setAppliedAt(app.getAppliedAt());
            return dto;
        }).toList();
    }
    @Override
    public List<JobApplicationForEmployerDto> getApplicationsForEmployer(Long employerId) {
        List<JobPost> employerJobs = jobRepository.findByUserId(employerId);
        List<JobApplicationForEmployerDto> result = new ArrayList<>();

        for (JobPost job : employerJobs) {
            List<JobApplication> applications = jobApplicationRepository.findByJob(job);
            for (JobApplication application : applications) {
                JobApplicationForEmployerDto dto = new JobApplicationForEmployerDto();
                dto.setApplicationId(application.getId());
                dto.setCandidateId(application.getCandidate().getId());
                dto.setCandidateEmail(application.getCandidate().getUser().getEmail());
                dto.setJobTitle(job.getTitle());
                dto.setApplicationStatus(application.getStatus().toString());
                dto.setResumeLink(application.getCandidate().getResumeLink());
                result.add(dto);
            }
        }

        return result;
    }

    @Override
    public String updateApplicationStatus(Long employerId, ApplicationStatusUpdateDto statusUpdateDto) {
        Optional<JobApplication> optional = jobApplicationRepository.findById(statusUpdateDto.getApplicationId());

        if (optional.isEmpty()) {
            throw new RuntimeException("Application not found");
        }

        JobApplication application = optional.get();

        // Check if the employer owns the job
        if (!application.getJob().getUserId().equals(employerId)) {
            throw new RuntimeException("Unauthorized to update this application");
        }

        try {
            ApplicationStatus newStatus = ApplicationStatus.valueOf(statusUpdateDto.getNewStatus().toUpperCase());
            application.setStatus(newStatus);
            jobApplicationRepository.save(application);
            return "Application status updated to " + newStatus;
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status. Allowed: SHORTLISTED, REJECTED");
        }
    }
    @Override
    public String updateInterviewStatus(Long employerId, InterviewStatusUpdateDto interviewStatusUpdateDto) {
        Optional<JobApplication> optional = jobApplicationRepository.findById(interviewStatusUpdateDto.getApplicationId());

        if (optional.isEmpty()) {
            throw new RuntimeException("Application not found");
        }

        JobApplication application = optional.get();

        // Check if the employer owns the job
        if (!application.getJob().getUserId().equals(employerId)) {
            throw new RuntimeException("Unauthorized to update this application");
        }

        try {
            InterviewStatus interviewStatus = InterviewStatus.valueOf(interviewStatusUpdateDto.getInterviewStatus().toUpperCase());
            application.setInterviewStatus(interviewStatus);
            jobApplicationRepository.save(application);
            sendInterviewStatusEmail(application, interviewStatus);

            return "Interview status updated to " + interviewStatus;
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status. Allowed: ACCEPTED, REJECTED");
        }
    }
    @Override
    public List<InterviewDetailsDto> getAcceptedInterviewsByJobId(Long jobId) {
        String employerId = String.valueOf(userService.getLoggedInUser().getUserId());
        // Check if the job exists and belongs to this employer
        JobPost job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));


        List<JobApplication> acceptedInterviews = jobApplicationRepository
                .findByJobIdAndInterviewStatus(jobId, InterviewStatus.ACCEPTED);

        List<InterviewDetailsDto> result = new ArrayList<>();
        for (JobApplication application : acceptedInterviews) {
            InterviewDetailsDto dto = new InterviewDetailsDto();
            dto.setCandidateId(application.getCandidate().getId());
            dto.setCandidateEmail(application.getCandidate().getUser().getEmail());
            dto.setAppliedAt(application.getAppliedAt());
            dto.setJobTitle(job.getTitle());
            result.add(dto);
        }

        return result;
    }

    private void sendApplicationConfirmationEmail(Candidate candidate, JobPost job) {
        String subject = "Job Application Confirmation";
        String body = "Dear " + userService.getLoggedInUser().getFirstName() + ",\n\n"
                + "You have successfully applied for the position of " + job.getTitle() + ".\n\n"
                + "We will notify you of any further updates.\n\n"
                + "Best regards,\nThe Job Portal Team";

        mailService.sendEmail(candidate.getUser().getEmail(), subject, body);
    }

    private void sendInterviewStatusEmail(JobApplication application, InterviewStatus interviewStatus) {
        Candidate candidate = application.getCandidate();
        String subject = "Interview Status Update";
        String body = "Dear " + userService.getLoggedInUser().getFirstName()  + ",\n\n"
                + "Your interview status for the position of " + application.getJob().getTitle() + " has been updated to: " + interviewStatus + ".\n\n"
                + "Best regards,\nThe Job Portal Team";

        mailService.sendEmail(candidate.getUser().getEmail(), subject, body);
    }

}

