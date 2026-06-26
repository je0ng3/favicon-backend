package com.capstone.favicon.dataset.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "trend", indexes = {
        @Index(name = "idx_trend_rank_date", columnList = "rank_date"),
        @Index(name = "idx_trend_dataset_rank_date", columnList = "dataset_id, rank_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Trend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate rankDate;

    private Integer rank;

    private String trendStatus; // "상승", "하락", "유지"

    @ManyToOne
    @JoinColumn(name = "dataset_id")
    private Dataset dataset;
}

