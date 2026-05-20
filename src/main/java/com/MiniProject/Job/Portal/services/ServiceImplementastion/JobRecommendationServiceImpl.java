package com.MiniProject.Job.Portal.services.ServiceImplementastion;

import com.MiniProject.Job.Portal.helperClass.JobPostSpecification;
import com.MiniProject.Job.Portal.model.dto.JobRecommendationDTO;
import com.MiniProject.Job.Portal.model.dto.RecommendedCandidateDto;
import com.MiniProject.Job.Portal.model.entity.Candidate;
import com.MiniProject.Job.Portal.model.entity.JobPost;
import com.MiniProject.Job.Portal.model.entity.PagedResponse;
import com.MiniProject.Job.Portal.repository.CandidateProfileRepository;
import com.MiniProject.Job.Portal.repository.JobPostRepository;
import com.MiniProject.Job.Portal.services.JobRecommendationService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class JobRecommendationServiceImpl implements JobRecommendationService {
    private final UserServiceImpl userService;
    private final JobPostRepository jobPostRepository;
    private final CandidateProfileRepository candidateRepository;

    public JobRecommendationServiceImpl(UserServiceImpl userService, JobPostRepository jobPostRepository, CandidateProfileRepository candidateRepository) {
        this.userService = userService;
        this.jobPostRepository = jobPostRepository;
        this.candidateRepository = candidateRepository;
    }


    @Override
    public PagedResponse<JobRecommendationDTO> recommendJobsForCandidate(int page, int size) {
        Candidate candidate = userService.getLoggedInUser().getCandidate();
        List<String> skills = candidate.getSkills();

        if (skills.isEmpty()) {
            throw new RuntimeException("No matching Job Found");
        }

        // Get all jobs that match at least one skill (remove pagination here)
        Specification<JobPost> spec = JobPostSpecification.hasMatchingSkills(skills);
        List<JobPost> jobPosts = jobPostRepository.findAll(spec);

        int maxPossibleScore = 2 + skills.size() + candidate.getDegree().size() + 2 + 2;

        List<JobRecommendationDTO> scoredJobDTOs = jobPosts.stream()
                .map(job -> {
                    int score = 0;

                    // Skills match
                    long matchedSkills = job.getSkills().stream()
                            .filter(skills::contains)
                            .count();
                    if (matchedSkills == 0) return null;
                    score += matchedSkills;

                    // Location match
                    if (job.getLocation() != null && job.getLocation().equalsIgnoreCase(candidate.getLocation())) {
                        score += 2;
                    }

                    // Degree match
                    long matchedDegrees = job.getDegree().stream()
                            .map(String::toLowerCase)
                            .filter(degree -> candidate.getDegree().stream()
                                    .map(String::toLowerCase)
                                    .collect(Collectors.toSet())
                                    .contains(degree))
                            .count();
                    score += matchedDegrees;

                    // Engagement type match
                    if (job.getEngagementType() != null && job.getEngagementType().equalsIgnoreCase(candidate.getPreferredEngagementType())) {
                        score += 2;
                    }

                    // Job type match
                    if (job.getJobType() != null && job.getJobType().equalsIgnoreCase(candidate.getPreferredJobType())) {
                        score += 2;
                    }

                    double matchPercentage = ((double) score / maxPossibleScore) * 100;
                    if (matchPercentage < 30) return null;

                    JobRecommendationDTO dto = new JobRecommendationDTO();
                    dto.setJobId(job.getId());
                    dto.setTitle(job.getTitle());
                    dto.setLocation(job.getLocation());
                    dto.setRequiredSkills(job.getSkills());
                    dto.setMatchingSkills(matchedSkills);
                    dto.setMatchScore(matchPercentage);

                    return dto;
                })
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingDouble(JobRecommendationDTO::getMatchScore).reversed())
                .collect(Collectors.toList());

        // Manual pagination after scoring
        int start = Math.min(page * size, scoredJobDTOs.size());
        int end = Math.min(start + size, scoredJobDTOs.size());
        List<JobRecommendationDTO> paginated = scoredJobDTOs.subList(start, end);

        return new PagedResponse<>(
                paginated,
                page,
                size,
                scoredJobDTOs.size(),
                (int) Math.ceil((double) scoredJobDTOs.size() / size),
                end == scoredJobDTOs.size()
        );
    }


    public List<RecommendedCandidateDto> recommendCandidatesForJob(Long jobId) {
        JobPost jobPost = jobPostRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));


        System.out.println("Job Location: " + jobPost.getLocation());
        System.out.println("Job Skills: " + jobPost.getSkills());
        System.out.println("Job Degree: " + jobPost.getDegree());

        // Calculate the maximum possible score for candidates
        int maxPossibleScore = 2 + jobPost.getSkills().size() + jobPost.getDegree().size() + 2 + 2; // Based on matching criteria

        // Retrieve all candidates (or you could filter based on role if needed)
        List<Candidate> candidates = candidateRepository.findAll();  // Fetch all candidates, or apply filters as needed
        System.out.println("candidate : "+candidates);
        System.out.println("Total Candidates: " + candidates.size());

        // Map candidates to DTOs and calculate match score
        List<RecommendedCandidateDto> scoredCandidates = candidates.stream()
                .map(candidate -> {
                    int score = 0;

                    // Location match
                    if (jobPost.getLocation() != null && jobPost.getLocation().equalsIgnoreCase(candidate.getLocation())) {
                        score += 2;
                    }

                    // Skill match
                    long matchedSkills = jobPost.getSkills().stream()
                            .map(String::toUpperCase)
                            .filter(skill -> candidate.getSkills().stream()
                                    .map(String::toUpperCase)
                                    .collect(Collectors.toSet())
                                    .contains(skill))
                            .count();
                    score += matchedSkills;

                    // Degree match
                    long matchedDegrees = jobPost.getDegree().stream()
                            .map(String::toLowerCase)
                            .filter(degree -> candidate.getDegree().stream()
                                    .map(String::toLowerCase)
                                    .collect(Collectors.toSet())
                                    .contains(degree))
                            .count();
                    score += matchedDegrees;

                    // Engagement type match
                    if (jobPost.getEngagementType() != null && jobPost.getEngagementType().equalsIgnoreCase(candidate.getPreferredEngagementType())) {
                        score += 2;
                    }

                    // Job type match
                    if (jobPost.getJobType() != null && jobPost.getJobType().equalsIgnoreCase(candidate.getPreferredJobType())) {
                        score += 2;
                    }

                    // Calculate the match percentage
                    double matchPercentage = ((double) score / maxPossibleScore) * 100;

                    // Only include candidates with at least 30% match
                    if (matchPercentage < 30) {
                        return null; // Filter out this candidate
                    }

                    // Map to DTO
                    return mapToDto(candidate, score);
                })
                .filter(Objects::nonNull) // Remove nulls (candidates that didn't meet the 30% threshold)
                .sorted(Comparator.comparingInt(RecommendedCandidateDto::getScore).reversed())
                .collect(Collectors.toList());

        return scoredCandidates;
    }

    private RecommendedCandidateDto mapToDto(Candidate candidate, int score) {
        return new RecommendedCandidateDto(
                candidate.getId(),
                candidate.getUser().getFirstName() + " " + candidate.getUser().getLastName(),
                candidate.getUser().getEmail(),
                candidate.getSkills(),
                candidate.getDegree(),
                candidate.getLocation(),
                candidate.getTotalExperience(),
                score
        );
    }


}
