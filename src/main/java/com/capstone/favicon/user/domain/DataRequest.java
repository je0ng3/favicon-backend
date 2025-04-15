package com.capstone.favicon.user.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name="data_request")
public class DataRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dataRequestId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String purpose;
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDate uploadDate;
    private String fileUrl;

    @Enumerated(EnumType.STRING)
    private ReviewStatus reviewStatus = ReviewStatus.PENDING;

    @ManyToOne
    @JoinColumn(name = "reviewed_by")
    private User reviewedBy;

    private LocalDate reviewDate;
    private String organization;

    public enum ReviewStatus {
        PENDING, APPROVED, REJECTED
    }
}
