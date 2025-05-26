package com.capstone.favicon.dataset.domain;

import com.capstone.favicon.dataset.domain.Dataset;
import com.capstone.favicon.dataset.domain.FileExtension;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
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
    @Column(name = "resource_type", nullable = true)
    private FileExtension type;

    @Column(name = "resource_url", nullable = false)
    private String resourceUrl;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @JoinColumn(name = "dataset_id", referencedColumnName = "dataset_id", nullable = false)
    private Dataset dataset;

    public Resource(Dataset dataset, String resourceName, FileExtension type, String resourceUrl) {
        this.dataset = dataset;
        this.resourceName = resourceName;
        this.type = type;
        this.resourceUrl = resourceUrl;
    }

    public Resource() {}
}
