package com.MiniProject.Job.Portal.services.ServiceImplementastion;

import com.MiniProject.Job.Portal.helperClass.JobPostSpecification;
import com.MiniProject.Job.Portal.model.dto.JobPostRequest;
import com.MiniProject.Job.Portal.model.entity.Candidate;
import com.MiniProject.Job.Portal.model.entity.JobPost;
import com.MiniProject.Job.Portal.repository.CandidateProfileRepository;
import com.MiniProject.Job.Portal.repository.JobPostRepository;
import com.MiniProject.Job.Portal.services.JobPostService;
import com.MiniProject.Job.Portal.services.MailService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class JobPostServiceImpl implements JobPostService {


    private final JobPostRepository jobPostRepository;
    private final UserServiceImpl userService;
    private final CandidateProfileRepository candidateProfileRepository;
    private final MailService mailService;

    public JobPostServiceImpl(JobPostRepository jobPostRepository, UserServiceImpl userService, CandidateProfileRepository candidateProfileRepository, MailService mailService) {
        this.jobPostRepository = jobPostRepository;
        this.userService = userService;
        this.candidateProfileRepository = candidateProfileRepository;
        this.mailService = mailService;
    }

    @Override
    public JobPost createJob(JobPostRequest request) {
        Long userID =  userService.getLoggedInUser().getUserId();
        JobPost job = new JobPost();
        job.setTitle(request.getTitle());
        job.setCreatedDate(LocalDateTime.now());
        job.setUserId(userID);
        job.setDescription(request.getDescription());
        job.setExperienceRange(request.getExperienceRange());
        job.setSkills(request.getSkills());
        job.setDegree(request.getDegree());
        job.setLocation(request.getLocation());
        job.setEngagementType(request.getEngagementType());
        job.setJobType(request.getJobType());
        JobPost saveJob =  jobPostRepository.save(job);
        sendNewJobPostEmail(saveJob);

        return saveJob;
    }

    @Override
    public JobPost updateJob(Long id, JobPostRequest request) {
        JobPost job = jobPostRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        if (request.getTitle() != null) {
            job.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            job.setDescription(request.getDescription());
        }
        if (request.getExperienceRange() != null) {
            job.setExperienceRange(request.getExperienceRange());
        }
        if (request.getSkills() != null) {
            job.setSkills(request.getSkills());
        }
        if (request.getDegree() != null) {
            job.setDegree(request.getDegree());
        }
        if (request.getLocation() != null) {
            job.setLocation(request.getLocation());
        }
        if (request.getEngagementType() != null) {
            job.setEngagementType(request.getEngagementType());
        }
        if (request.getJobType() != null) {
            job.setJobType(request.getJobType());
        }
        return jobPostRepository.save(job);
    }

    @Override
    public void deleteJob(Long id) {
        jobPostRepository.deleteById(id);
    }


    @Override
    public List<JobPost> getJobsByUserId(Long userId) {
        return jobPostRepository.findAllById(Collections.singleton(userId));
    }


    @Override
    public List<JobPost> getAllJobs() {
        return jobPostRepository.findAll();
    }



    public Page<JobPost> getFilteredJobs(String title,String keyword, String location, String skills, LocalDate createdDate, Pageable pageable) {
        Specification<JobPost> spec = JobPostSpecification.getJobPostsByCriteria(title, location, keyword, skills, createdDate);
        return jobPostRepository.findAll(spec, pageable);
    }


    @Override
    public List<JobPost> getJobsByEmployerId(Long employerId) {

        return jobPostRepository.findByUserId(employerId);
    }


    //admin
    @Override
    public JobPost getJobById(Long jobId) {
        return jobPostRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found with ID: " + jobId));
    }

    @Override
    public void deleteJobById(Long jobId) {
        JobPost job = jobPostRepository.findById(jobId)
                .orElseThrow(() -> new UsernameNotFoundException("Job with ID " + jobId + " not found"));
        jobPostRepository.delete(job);
    }

    private void sendNewJobPostEmail(JobPost jobPost) {
        List<Candidate> candidates = candidateProfileRepository.findCandidatesBySkillsAndLocation(jobPost.getSkills(), jobPost.getLocation());
        System.out.println("Candidates found: " + candidates.size());
        for (Candidate candidate : candidates) {
            String candidateEmail = candidate.getUser().getEmail();
            if (candidateEmail != null && !candidateEmail.isEmpty()) {
                try {
                    String subject = "New Job Post: " + jobPost.getTitle();
                    String body = "Dear " + candidate.getUser().getFirstName() + ",\n\n"
                            + "A new job has been posted that matches your profile:\n\n"
                            + "Job Title: " + jobPost.getTitle() + "\n"
                            + "Location: " + jobPost.getLocation() + "\n\n"
                            + "Please visit the portal to apply.\n\n"
                            + "Best regards,\nThe Job Portal Team";
                    mailService.sendEmail(candidateEmail, subject, body);
                } catch (Exception e) {
                    System.out.println("Failed to send email to " + candidateEmail);
                    e.printStackTrace();
                }
            } else {
                System.out.println("Candidate email is missing for: " + candidate.getUser().getFirstName());
            }
        }
    }
}


