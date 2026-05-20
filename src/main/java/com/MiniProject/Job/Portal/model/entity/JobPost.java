package com.MiniProject.Job.Portal.model.entity;


import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class JobPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private LocalDateTime createdDate;

    private Long userId;

    @Column(length = 1000)
    private String description;

    private String experienceRange;

    @ElementCollection
    private List<String> skills;

    @ElementCollection
    private List<String> degree;

    private String location;

    private String engagementType;

    private String jobType;
}

