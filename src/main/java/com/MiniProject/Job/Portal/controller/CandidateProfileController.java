package com.MiniProject.Job.Portal.controller;

import com.MiniProject.Job.Portal.model.dto.CandidateProfileRequestDto;
import com.MiniProject.Job.Portal.model.dto.JobRecommendationDTO;
import com.MiniProject.Job.Portal.model.entity.Candidate;
import com.MiniProject.Job.Portal.model.entity.PagedResponse;
import com.MiniProject.Job.Portal.services.CandidateProfileService;
import com.MiniProject.Job.Portal.services.JobRecommendationService;
import com.MiniProject.Job.Portal.services.ServiceImplementastion.CandidateProfileServiceImpl;
import com.MiniProject.Job.Portal.services.ServiceImplementastion.JobRecommendationServiceImpl;
import com.MiniProject.Job.Portal.services.ServiceImplementastion.UserServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/candidate")
public class CandidateProfileController {

    private final CandidateProfileService candidateProfileService;
    private final JobRecommendationService jobRecommendationService;
    private final UserServiceImpl userService;


    public CandidateProfileController(CandidateProfileServiceImpl candidateProfileService,
                                      JobRecommendationServiceImpl jobRecommendationServiceImpl, JobRecommendationService jobRecommendationService,
                                      UserServiceImpl userService) {
        this.candidateProfileService = candidateProfileService;
        this.jobRecommendationService = jobRecommendationService;
        this.userService = userService;
    }


    @PostMapping("/update")
    public ResponseEntity<Candidate> createOrUpdateProfile(
            Authentication authentication,
            @RequestBody CandidateProfileRequestDto requestDto) {
        Candidate profile = candidateProfileService.createOrUpdateProfile(requestDto);
        return ResponseEntity.ok(profile);
    }

    @GetMapping
    public ResponseEntity<Candidate> getProfile(Authentication authentication) {
        Long candidateId = Long.valueOf(authentication.getName());
        return ResponseEntity.ok(candidateProfileService.getProfileByCandidateId(candidateId));
    }

    @GetMapping("/recommendations")
    public ResponseEntity<PagedResponse<JobRecommendationDTO>> getJobRecommendations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(jobRecommendationService.recommendJobsForCandidate(page,size));
    }

    @PostMapping("/uploadResume")
    public ResponseEntity<String> uploadResume(@RequestParam("file") MultipartFile file) {
        try {
            candidateProfileService.uploadResume(file);
            return ResponseEntity.ok("Resume uploaded successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload resume");
        }
    }

    @GetMapping("/{candidateId}/downloadResume")
    public ResponseEntity<byte[]> downloadResume(@PathVariable Long candidateId) {

        try {
            return candidateProfileService.downloadResume(candidateId);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


}
