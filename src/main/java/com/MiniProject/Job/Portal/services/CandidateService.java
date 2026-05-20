package com.MiniProject.Job.Portal.services;

import com.MiniProject.Job.Portal.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CandidateService extends JpaRepository<User, Long> {

      User getCandidateByEmail(String email);

}
