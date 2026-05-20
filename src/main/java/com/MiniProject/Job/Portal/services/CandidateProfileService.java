package com.MiniProject.Job.Portal.services;


import com.MiniProject.Job.Portal.model.dto.CandidateProfileRequestDto;
import com.MiniProject.Job.Portal.model.entity.Candidate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;

public interface CandidateProfileService {

    Candidate createOrUpdateProfile(CandidateProfileRequestDto requestDto);

    Candidate getProfileByCandidateId(Long candidateId);
    void uploadResume(MultipartFile file) throws IOException;


    ResponseEntity<byte[]> downloadResume(Long candidateId) throws MalformedURLException;
}
