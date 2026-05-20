package com.MiniProject.Job.Portal.repository;


import com.MiniProject.Job.Portal.model.entity.Candidate;
import com.MiniProject.Job.Portal.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CandidateProfileRepository extends JpaRepository<Candidate, Long> {
    Optional<Candidate> findById(Long id);

    Optional<Candidate> findByUser(User user);
    Optional<Candidate> findByUser_UserId(Long userId);


    @Query("SELECT DISTINCT c FROM Candidate c JOIN c.skills s WHERE c.location = :location AND s IN :skills")
    List<Candidate> findCandidatesBySkillsAndLocation(@Param("skills") List<String> skills, @Param("location") String location);
}
