package com.MiniProject.Job.Portal.repository;

import com.MiniProject.Job.Portal.model.entity.Candidate;
import com.MiniProject.Job.Portal.model.entity.InterviewStatus;
import com.MiniProject.Job.Portal.model.entity.JobApplication;
import com.MiniProject.Job.Portal.model.entity.JobPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    List<JobApplication> findByCandidate(Candidate candidate);
    List<JobApplication> findByJob(JobPost job);
    Optional<JobApplication> findByCandidateAndJob(Candidate candidate, JobPost job);
    List<JobApplication> findByJobIdAndInterviewStatus(Long jobId, InterviewStatus status);

}
