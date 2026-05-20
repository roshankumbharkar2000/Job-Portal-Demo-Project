package com.MiniProject.Job.Portal.controller;
import com.MiniProject.Job.Portal.model.dto.JobPostRequest;
import com.MiniProject.Job.Portal.model.entity.JobPost;
import com.MiniProject.Job.Portal.services.JobPostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/job")
public class JobPostController {


    private final JobPostService jobPostService;

    public JobPostController(JobPostService jobPostService) {
        this.jobPostService = jobPostService;
    }

    @PostMapping("/employer/create")
    public ResponseEntity<JobPost> create(@RequestBody JobPostRequest request) {
        return ResponseEntity.ok(jobPostService.createJob(request));
    }

    @PutMapping("/employer/update/{id}")
    public ResponseEntity<JobPost> update(@PathVariable Long id, @RequestBody JobPostRequest request) {
        return ResponseEntity.ok(jobPostService.updateJob(id, request));
    }

    @DeleteMapping("/employer/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        jobPostService.deleteJob(id);

        Map<String , String> message= new HashMap<>();
        message.put("message","Job post deleted successfully.");
        return ResponseEntity.ok(message);
    }

    @GetMapping("/employer/employer")
    public ResponseEntity<List<JobPost>> getJobsByEmployer() {
        List<JobPost> jobs = jobPostService.getAllJobs();
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/employer/{userId}")
    public ResponseEntity<?> getJobsByEmployer(@PathVariable Long userId) {
        List<JobPost> jobs = jobPostService.getJobsByEmployerId(userId);

        if (jobs != null && !jobs.isEmpty()) {
            return new ResponseEntity<>(jobs, HttpStatus.OK);
        }
        return new ResponseEntity<>("No jobs found for this employer", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/employer/search")
    public ResponseEntity<Page<JobPost>> searchJobs(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String skills,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdDate") String sortBy
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<JobPost> jobs = jobPostService.getFilteredJobs(title,keyword, location, skills, createdDate,pageable);
        return ResponseEntity.ok(jobs);
    }


    @GetMapping("/jobs")
    public ResponseEntity<List<JobPost>> getAllJobs() {
        return ResponseEntity.ok(jobPostService.getAllJobs());
    }


    @GetMapping("/job-by-id/{jobId}")
    public ResponseEntity<JobPost> getJobByJobId(@PathVariable("jobId") Long jobId) {
        JobPost jobPost = jobPostService.getJobById(jobId);
        return ResponseEntity.ok(jobPost);
    }

    @DeleteMapping("/delete-job/{jobId}")
    public ResponseEntity<String> deleteJobById(@PathVariable("jobId") Long jobId) {
        jobPostService.deleteJobById(jobId);
        return ResponseEntity.ok("Job with ID " + jobId + " deleted successfully.");
    }
}

