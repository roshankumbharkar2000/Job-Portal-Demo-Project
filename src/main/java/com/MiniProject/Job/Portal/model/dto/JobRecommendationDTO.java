package com.MiniProject.Job.Portal.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class JobRecommendationDTO {
    private Long jobId;
    private String title;
    private String location;
    private List<String> requiredSkills;
    private long matchingSkills;
    private double matchScore;
}
