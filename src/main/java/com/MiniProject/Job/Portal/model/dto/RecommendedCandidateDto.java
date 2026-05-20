package com.MiniProject.Job.Portal.model.dto;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RecommendedCandidateDto {
    private Long candidateId;
    private String name;
    private String email;
    private List<String> skills;
    private List<String> degree;
    private String location;
    private String totalExperience;
    private int matchScore;
    private int score;
    public RecommendedCandidateDto(Long candidateId, String name, String email,
                                   List<String> skills, List<String> degree, String location,
                                   String totalExperience, int score) {
        this.candidateId = candidateId;
        this.name = name;
        this.email = email;
        this.skills = skills;
        this.degree = degree;
        this.location = location;
        this.totalExperience = totalExperience;
        this.score = score;
    }

}
