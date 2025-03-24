package com.capstone.favicon.dataset.domain;

import com.capstone.favicon.dataset.domain.Dataset;
import com.capstone.favicon.dataset.domain.FileExtension;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "resource")
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resource_id")
    private Long resourceId;

    @Column(name = "resource_name", nullable = false)
    private String resourceName;

    @Enumerated(EnumType.STRING)
    @Column(name = "resource_type", nullable = false)
    private FileExtension type;

    @Column(name = "resource_url", nullable = false)
    private String resourceUrl;

    @OneToOne
    @JoinColumn(name = "dataset_id", referencedColumnName = "dataset_id", nullable = false) // FK
    private Dataset dataset;
}
