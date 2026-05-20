package com.MiniProject.Job.Portal.controller;

import com.MiniProject.Job.Portal.model.dto.InterviewResponseDto;
import com.MiniProject.Job.Portal.model.dto.InterviewScheduleRequestDto;
import com.MiniProject.Job.Portal.model.dto.RescheduleInterviewRequestDto;
import com.MiniProject.Job.Portal.services.InterviewService;
import com.MiniProject.Job.Portal.services.ServiceImplementastion.UserServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interview")
public class InterviewController {

    private final InterviewService interviewService;
    private final UserServiceImpl userService;

    public InterviewController(InterviewService interviewService, UserServiceImpl userService) {
        this.interviewService = interviewService;
        this.userService = userService;
    }

    @PostMapping("/employer/schedule")
    public ResponseEntity<String> scheduleInterview(@RequestBody InterviewScheduleRequestDto request) {
        Long employerId = userService.getLoggedInUser().getUserId();
        return ResponseEntity.ok(interviewService.scheduleInterview(employerId, request));
    }

    @GetMapping("/employer/job/{jobId}")
    public ResponseEntity<List<InterviewResponseDto>> getInterviewsByJob(
            @PathVariable Long jobId) {
        Long employerId = userService.getLoggedInUser().getUserId();
        List<InterviewResponseDto> interviews = interviewService.getInterviewsByJobId(jobId, employerId);
        return ResponseEntity.ok(interviews);
    }


    //Reschedule Interview (When Candidate Requests It)
    @PutMapping("/employer/reschedule/employer")
    public ResponseEntity<String> rescheduleInterviewByEmployer(
            @RequestBody RescheduleInterviewRequestDto request) {
        Long employerId = userService.getLoggedInUser().getUserId();
        String message = interviewService.rescheduleInterviewByEmployer(request, employerId);
        return ResponseEntity.ok(message);
    }

    @PutMapping("/candidate/reschedule/request")
    public ResponseEntity<String> requestRescheduleByCandidate(@RequestBody RescheduleInterviewRequestDto request) {
        Long candidateId = userService.getLoggedInUser().getUserId();
        String message = interviewService.requestRescheduleByCandidate(request, candidateId);
        return ResponseEntity.ok(message);
    }

    //Confirm Interview
    @PutMapping("/candidate/confirm/{interviewId}")
    public ResponseEntity<String> confirmInterview(@PathVariable Long interviewId) {
        Long candidateId = userService.getLoggedInUser().getUserId();
        String result = interviewService.confirmInterview(interviewId, candidateId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/candidate/interviews")
    public ResponseEntity<List<InterviewResponseDto>> getInterviewsForCandidate() {
        return ResponseEntity.ok(interviewService.getInterviewsForCandidate());
    }

}
