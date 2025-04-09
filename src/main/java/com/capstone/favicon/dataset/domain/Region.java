package com.capstone.favicon.dataset.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "region")
@Getter
@Setter
public class Region {
    @Id
    @Column(name = "region_name", nullable = false)
    private String regionName;

}
