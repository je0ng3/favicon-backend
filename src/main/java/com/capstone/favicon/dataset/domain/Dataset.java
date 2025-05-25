package com.capstone.favicon.dataset.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name="dataset")
public class Dataset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dataset_id")
    private Long datasetId;

    private String organization;
    private String title;
    private String description;
    private LocalDate createdDate;
    private LocalDate updateDate;
    private Integer view;
    private Integer download;
    private String license;
    private String keyword;
    private Boolean analysis;

    @Column(name = "name")
    private String name;

    @Column(name = "s3Key", unique = true)
    private String s3Key;

    @ManyToOne
    @JoinColumn(name = "dataset_theme_id", nullable = false)
    private DatasetTheme datasetTheme;

    public Dataset(DatasetTheme theme, String name, String title, String organization, String description) {
        this.datasetTheme = theme;
        this.name = name;
        this.title = title;
        this.organization = organization;
        this.description = description;
    }

    public Dataset(DatasetTheme theme, String name, String title, String organization, String description, String s3Key) {
        this.datasetTheme = theme;
        this.name = name;
        this.title = title;
        this.organization = organization;
        this.description = description;
        this.s3Key = s3Key;
        this.createdDate = LocalDate.now();
    }

    protected Dataset() {}

    @OneToOne(mappedBy = "dataset", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Resource resource;

    @OneToMany(mappedBy = "dataset", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Download> downloadSet;
}
