package com.capstone.favicon.Dataset.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "dataset_theme")
public class DatasetTheme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long datasetThemeId;

    @ManyToOne
    @JoinColumn(name = "dataset_id", nullable = false)
    private Dataset dataset;

    private String theme;
    private String region;
    private Integer dataYear;
    private String fileType;
}