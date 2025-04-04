package com.capstone.favicon.dataset.domain;

import com.capstone.favicon.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="download")
public class Download {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "download_id")
    private Long downloadId;

    @ManyToOne
    @JoinColumn(name = "dataset_id", referencedColumnName = "dataset_id")
    private Dataset dataset;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
