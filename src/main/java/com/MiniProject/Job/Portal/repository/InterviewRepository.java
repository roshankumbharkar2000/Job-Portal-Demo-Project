package com.MiniProject.Job.Portal.repository;
import com.MiniProject.Job.Portal.model.entity.Candidate;
import com.MiniProject.Job.Portal.model.entity.Interview;
import com.MiniProject.Job.Portal.model.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface InterviewRepository extends JpaRepository<Interview, Long> {

    List<Interview> findByJobApplication_Job_Id(Long jobId);

    List<Interview> findByJobApplication_Candidate_Id(Long candidateId);

    Optional<Interview> findByJobApplication(JobApplication application);
    List<Interview> findAllByJobApplicationIn(List<JobApplication> jobApplications);
//  List<Interview> findByApplication_Candidate(Candidate candidate);
    List<Interview> findByJobApplication_Candidate(Candidate candidate);

}
