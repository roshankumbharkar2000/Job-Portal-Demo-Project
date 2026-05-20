package com.MiniProject.Job.Portal.repository;

import com.MiniProject.Job.Portal.model.entity.PasswordResetToken;
import com.MiniProject.Job.Portal.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    void deleteByUser(User user);

    void deleteByToken(String token);
}

