package com.MiniProject.Job.Portal.repository;

import com.MiniProject.Job.Portal.model.entity.Role;
import com.MiniProject.Job.Portal.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);
    Optional<User> findByResetToken(String token);
    List<User> findByRole(Role role);
}
