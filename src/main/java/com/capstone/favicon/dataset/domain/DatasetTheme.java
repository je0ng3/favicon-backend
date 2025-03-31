package com.capstone.favicon.dataset.domain;

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
    @Column(name = "dataset_theme_id")
    private Long datasetThemeId;

    private String theme;
    private String region;
    private Integer dataYear;
    private String fileType;
}
