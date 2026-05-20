package com.MiniProject.Job.Portal.repository;

import com.MiniProject.Job.Portal.model.dto.JobRecommendationDTO;
import com.MiniProject.Job.Portal.model.entity.PagedResponse;

public interface JobRecommendationRepository {


    PagedResponse<JobRecommendationDTO> recommendJobsForCandidate(int page, int size);
}
