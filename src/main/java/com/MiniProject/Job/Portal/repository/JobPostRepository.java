package com.MiniProject.Job.Portal.repository;


import com.MiniProject.Job.Portal.model.entity.JobPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface JobPostRepository extends JpaRepository<JobPost, Long> , JpaSpecificationExecutor<JobPost> {
    List<JobPost> findByUserId(Long userId);
    JobPost getJobById(Long id);

    Page<JobPost> findAll(Specification<JobPost> spec, Pageable pageable);

}

