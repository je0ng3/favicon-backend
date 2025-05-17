package com.capstone.favicon.dataset.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "trend")
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

