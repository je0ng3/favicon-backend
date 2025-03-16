package com.capstone.favicon.dataset.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="resource")
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resource_id")
    Long resourceId;

    @Column(name = "resource_name")
    private String resourceName;

    @Enumerated(EnumType.STRING) // Enum
    @Column(name = "type", columnDefinition = "type")
    private FileExtension type;

    @Column(name = "resource_url")
    private String resourceUrl;

    @OneToOne
    @JoinColumn(name = "dataset_id", referencedColumnName = "dataset_id") // FK
    private Dataset dataset;

}
