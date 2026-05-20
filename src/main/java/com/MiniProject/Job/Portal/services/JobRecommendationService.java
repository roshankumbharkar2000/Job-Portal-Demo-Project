package com.MiniProject.Job.Portal.services;

import com.MiniProject.Job.Portal.model.dto.JobRecommendationDTO;
import com.MiniProject.Job.Portal.model.entity.PagedResponse;

public interface JobRecommendationService {
    PagedResponse<JobRecommendationDTO> recommendJobsForCandidate(int page, int size);

}
