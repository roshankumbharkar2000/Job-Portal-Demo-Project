package com.MiniProject.Job.Portal.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;
@Setter
@Getter
@Entity
@Data
@Table(name="candidate_profile")
public class Candidate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "user_id", unique = true)
    @JsonBackReference
    @ToString.Exclude
    private User user;

    private String resumeLink;
    private String designation;
    @ElementCollection
    private List<String> skills;

    @ElementCollection
    @CollectionTable(name = "Candidate_degree", joinColumns = @JoinColumn(name = "user_id"))
    private List<String> degree;

    private String location;
    private String preferredEngagementType;
    private String preferredJobType;
    private String totalExperience;

    private LocalDateTime createdDate = LocalDateTime.now();

    private String resumeFile;

    @Lob
    private byte[] resumeFileUrl;



}
