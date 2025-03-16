package com.capstone.favicon.download.domain;

import com.capstone.favicon.dataset.domain.Dataset;
import jakarta.persistence.*;

@Entity
public class Download {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "download_id")
    private Long downloadId;

    @ManyToOne
    @JoinColumn(name = "dataset_id", referencedColumnName = "dataset_id")
    private Dataset dataset;

    @Column(name = "user_id")
    private Long userId; //user domain이 생기면 FK로 변경 필요
}
