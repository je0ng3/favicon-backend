package com.capstone.favicon.user.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "scrap", indexes = {
        @Index(name = "idx_scrap_user_id", columnList = "user_id")
})
public class Scrap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scrapId;

    private Long userId;
    private Long datasetId;
    private String title;
    private String theme;
}

