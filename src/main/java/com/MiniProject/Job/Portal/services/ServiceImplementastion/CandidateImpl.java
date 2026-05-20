package com.MiniProject.Job.Portal.services.ServiceImplementastion;

import com.MiniProject.Job.Portal.model.entity.User;
import com.MiniProject.Job.Portal.repository.UserRepo;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service

public class CandidateImpl {

    private final UserRepo candidateRepo;
    private final PasswordEncoder passwordEncoder;

    public CandidateImpl(UserRepo candidateRepo, PasswordEncoder passwordEncoder) {
        this.candidateRepo = candidateRepo;
        this.passwordEncoder = passwordEncoder;
    }


    public User registerCandidate(User candidate) {
        if (candidateRepo.existsByEmail(candidate.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        candidate.setPassword(passwordEncoder.encode(candidate.getPassword())); // Hash Password
        return candidateRepo.save(candidate);
    }




}






