package com.capstone.favicon.dataset.domain;

import com.capstone.favicon.download.domain.Download;
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

    @OneToOne(mappedBy = "dataset", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Resource resource;

    @OneToMany(mappedBy = "dataset", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Download> downloadSet;
}