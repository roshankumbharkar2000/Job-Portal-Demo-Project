package com.MiniProject.Job.Portal.model.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@Getter
public class CandidateProfileRequestDto {
    private String resumeLink;
    private String experience;
    private String designation;
    private List<String> degrees;
    private String location;
    private String preferredEngagementType; //  FULL_TIME, PART_TIME
    private String preferredJobType;        //   REMOTE, ONSITE
    private List<String> skills;
    private String totalExperience;
}
