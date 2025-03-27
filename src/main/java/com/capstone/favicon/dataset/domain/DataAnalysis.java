package com.capstone.favicon.dataset.domain;

import com.capstone.favicon.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name="data_analysis")
public class DataAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long analysisId;

    @ManyToOne
    @JoinColumn(name = "dataset_id", nullable = false)
    private Dataset dataset;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private LocalDate analysisDate;

    @Column(columnDefinition = "TEXT")
    private String analysisResult;

    /* private String resultUrl;

    @Enumerated(EnumType.STRING)
    private AnalysisStatus status = AnalysisStatus.REQUESTED;

    public enum AnalysisStatus {
        REQUESTED, IN_PROGRESS, COMPLETED
    } */
}

