package com.capstone.favicon.Dataset.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "dataset")
public class Dataset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long datasetId;

    private String organization;
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private LocalDate createdDate;
    private LocalDate updateDate;
    private Integer view;
    private Integer download;
    private String license;

    @Column(columnDefinition = "TEXT")
    private String keyword;

    private Boolean analysis;
}
