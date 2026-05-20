package com.MiniProject.Job.Portal.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class Interview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private JobApplication jobApplication;

    private LocalDateTime scheduledAt;

    private String mode;

    private String locationorLink;

    @Enumerated(EnumType.STRING)
    private InterviewStatus status;

    private String rescheduleNote;
    private LocalDateTime reScheduledAt;

    @ManyToOne
    private JobApplication application;

    private String note;

}
