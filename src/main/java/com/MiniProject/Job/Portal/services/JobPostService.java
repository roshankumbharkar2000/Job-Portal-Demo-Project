package com.MiniProject.Job.Portal.services;

import com.MiniProject.Job.Portal.model.dto.JobPostRequest;
import com.MiniProject.Job.Portal.model.entity.JobPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface JobPostService {
    JobPost createJob(JobPostRequest request);
    JobPost updateJob(Long id, JobPostRequest request);
    void deleteJob(Long id);

    List<JobPost> getJobsByUserId(Long userId);



    List<JobPost> getAllJobs();

    Page<JobPost> getFilteredJobs(String title,String keyword, String location, String skills, LocalDate createdDate, Pageable pageable);


    List<JobPost> getJobsByEmployerId(Long employerId);

    //admin
    JobPost getJobById(Long jobId);

    void deleteJobById(Long jobId);
}
