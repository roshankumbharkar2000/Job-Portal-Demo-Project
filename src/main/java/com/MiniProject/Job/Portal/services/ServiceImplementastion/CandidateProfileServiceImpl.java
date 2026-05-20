package com.MiniProject.Job.Portal.services.ServiceImplementastion;


import com.MiniProject.Job.Portal.model.dto.CandidateProfileRequestDto;
import com.MiniProject.Job.Portal.model.entity.Candidate;
import com.MiniProject.Job.Portal.model.entity.EngagementType;
import com.MiniProject.Job.Portal.model.entity.JobType;
import com.MiniProject.Job.Portal.model.entity.User;
import com.MiniProject.Job.Portal.repository.CandidateProfileRepository;
import com.MiniProject.Job.Portal.services.CandidateProfileService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@Service
public class CandidateProfileServiceImpl implements CandidateProfileService {

    private final CandidateProfileRepository candidateProfileRepository;
    private final UserServiceImpl userService;
    private final Path rootLocation = Paths.get("uploads");

    public CandidateProfileServiceImpl(CandidateProfileRepository candidateProfileRepository, UserServiceImpl userService) {
        this.candidateProfileRepository = candidateProfileRepository;
        this.userService = userService;

    }


    @Override
    public Candidate createOrUpdateProfile(CandidateProfileRequestDto requestDto) {

        User user = userService.getLoggedInUser();
        Candidate profile = candidateProfileRepository.findByUser(user)
                .orElse(new Candidate());

        if (profile.getUser() == null) {
            profile.setUser(user);
        }

        profile.setResumeLink(requestDto.getResumeLink());
        profile.setSkills(requestDto.getSkills());
        profile.setTotalExperience(requestDto.getExperience());
        profile.setDegree(requestDto.getDegrees());
        profile.setCreatedDate(LocalDateTime.now());

        try {
            profile.setPreferredEngagementType(
                    String.valueOf(EngagementType.valueOf(requestDto.getPreferredEngagementType().toUpperCase()))
            );
            profile.setPreferredJobType(
                    String.valueOf(JobType.valueOf(requestDto.getPreferredJobType().toUpperCase()))
            );
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid JobType or EngagementType provided.");
        }

        profile.setDesignation(requestDto.getDesignation());
        profile.setLocation(requestDto.getLocation());

        return candidateProfileRepository.save(profile);
    }


    @Override
    public Candidate getProfileByCandidateId(Long candidateId) {
        return candidateProfileRepository.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Candidate profile not found"));
    }

    @Override
    public void uploadResume(MultipartFile file) throws IOException {
        Long candidateId = userService.getLoggedInUser().getCandidate().getId();
        Candidate candidate = candidateProfileRepository.findById(candidateId)
                .orElseThrow(() -> new UsernameNotFoundException("Candidate not found"));

        // Update candidate record with the file path
        candidate.setResumeFile(file.getOriginalFilename());;
        candidate.setResumeFileUrl(file.getBytes());
        candidateProfileRepository.save(candidate);
    }

    @Override
    public ResponseEntity<byte[]> downloadResume(Long candidateId) throws MalformedURLException {
        Candidate candidate = candidateProfileRepository.findById(candidateId)
                .orElseThrow(() -> new UsernameNotFoundException("Candidate not found"));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + candidate.getResumeFile() + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(candidate.getResumeFileUrl());

    }
}
