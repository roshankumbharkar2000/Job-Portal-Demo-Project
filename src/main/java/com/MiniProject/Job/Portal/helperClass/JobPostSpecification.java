package com.MiniProject.Job.Portal.helperClass;


import com.MiniProject.Job.Portal.model.entity.Candidate;
import com.MiniProject.Job.Portal.model.entity.JobPost;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JobPostSpecification {

    public static Specification<JobPost> getJobPostsByCriteria(String title, String keyword, String location, String skills, LocalDate createdDate) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (title != null && !title.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("title")),
                        "%" + title.toLowerCase() + "%"
                ));
            }

            if (keyword != null && !keyword.isEmpty()) {
                String pattern = "%" + keyword.toLowerCase() + "%";
                Predicate titlePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), pattern);
                Predicate descPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), pattern);
                predicates.add(criteriaBuilder.or(titlePredicate, descPredicate));
            }

            if (location != null && !location.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("location")),
                        "%" + location.toLowerCase() + "%"
                ));
            }

            if (skills != null && !skills.trim().isEmpty()) {
                Join<Object, Object> skillJoin = root.join("skills", JoinType.INNER);
                predicates.add(skillJoin.in(skills));
            }

            if (createdDate != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("createdDate"),
                        createdDate
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<JobPost> getJobPostRecommendationSpec(Candidate candidate) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Location
            if (candidate.getLocation() != null && !candidate.getLocation().isEmpty()) {
                predicates.add(cb.like(
                        cb.lower(root.get("location")),
                        "%" + candidate.getLocation().toLowerCase() + "%"
                ));
            }

            // Degrees (JobPost has @ElementCollection for degrees)
            if (candidate.getDegree() != null && !candidate.getDegree().isEmpty()) {
                Join<JobPost, String> degreeJoin = root.join("degree", JoinType.LEFT);
                predicates.add(degreeJoin.in(candidate.getDegree()));
            }

            // Skills (JobPost has @ElementCollection for skills)
            if (candidate.getSkills() != null && !candidate.getSkills().isEmpty()) {
                Join<JobPost, String> skillJoin = root.join("skills", JoinType.LEFT);
                predicates.add(skillJoin.in(candidate.getSkills()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<JobPost> hasMatchingSkills(List<String> candidateSkills) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (candidateSkills == null || candidateSkills.isEmpty()) {
                return cb.disjunction();
            }

            Join<JobPost, String> skillJoin = root.join("skills"); // Ensure `@ElementCollection` or @OneToMany
            CriteriaBuilder.In<String> inClause = cb.in(skillJoin);
            for(String skill : candidateSkills){
                inClause.value(skill);
            }

            query.distinct(true);
            return inClause;


        };
    }




}
