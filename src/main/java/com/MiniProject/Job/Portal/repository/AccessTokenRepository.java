package com.MiniProject.Job.Portal.repository;

import com.MiniProject.Job.Portal.model.entity.AccessToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccessTokenRepository extends JpaRepository
        <AccessToken, Long> {
    boolean existsByToken(String token);

    @Transactional
    void deleteByToken(String token);
}
