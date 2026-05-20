package com.MiniProject.Job.Portal.controller;


import com.MiniProject.Job.Portal.model.dto.*;
import com.MiniProject.Job.Portal.model.entity.JobApplication;
import com.MiniProject.Job.Portal.services.JobApplicationService;
import com.MiniProject.Job.Portal.services.ServiceImplementastion.JobRecommendationServiceImpl;
import com.MiniProject.Job.Portal.services.ServiceImplementastion.UserServiceImpl;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/job")
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;
    private final UserServiceImpl userService;
    private final JobRecommendationServiceImpl jobRecommendationService;

    public JobApplicationController( JobApplicationService jobApplicationService, UserServiceImpl userService, JobRecommendationServiceImpl jobRecommendationService) {
        this.jobApplicationService = jobApplicationService;
        this.userService = userService;
        this.jobRecommendationService = jobRecommendationService;
    }

    @PostMapping("/candidate/apply")
    public ResponseEntity<JobApplication> applyForJob(@RequestBody JobApplicationRequestDto request) {
        return ResponseEntity.ok(jobApplicationService.applyForJob(request));
    }

    @GetMapping("/candidate/my-jobs")
    public List<AppliedJobResponseDto> getMyAppliedJobs() {
        return jobApplicationService.getAppliedJobsForCandidate();
    }

    @GetMapping("/employer/applications")
    public ResponseEntity<List<JobApplicationForEmployerDto>> getApplications(Authentication authentication) {
        Long user = userService.getLoggedInUser().getUserId();
        List<JobApplicationForEmployerDto> applications = jobApplicationService.getApplicationsForEmployer(user);
        return ResponseEntity.ok(applications);
    }

    @PutMapping("/employer/application/status")
    public ResponseEntity<String> updateApplicationStatus(
            Authentication authentication,
            @RequestBody ApplicationStatusUpdateDto statusUpdateDto) {

        Long user = userService.getLoggedInUser().getUserId();
        String response = jobApplicationService.updateApplicationStatus(user, statusUpdateDto);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/employer/interview/status")
    public ResponseEntity<String> updateInterviewStatus(
            Authentication authentication,
            @RequestBody InterviewStatusUpdateDto statusUpdateDto) {

//        Long employerId = Long.valueOf(authentication.getName());
        Long user = userService.getLoggedInUser().getUserId();
        String response = jobApplicationService.updateInterviewStatus(user, statusUpdateDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/employer/job/{jobId}")
    public ResponseEntity<List<InterviewDetailsDto>> getAcceptedInterviewsByJobId(
            @PathVariable Long jobId)
    {
        List<InterviewDetailsDto> interviews = jobApplicationService.getAcceptedInterviewsByJobId(jobId);
        return ResponseEntity.ok(interviews);
    }


    @GetMapping("/employer/recommend-candidates/{jobId}")
    public ResponseEntity<List<RecommendedCandidateDto>> recommendCandidates(@PathVariable Long jobId) {
        System.out.println("jobId "+jobId);
        return ResponseEntity.ok(jobRecommendationService.recommendCandidatesForJob(jobId));
    }


}

