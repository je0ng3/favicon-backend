package com.capston.favicon.user.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@Entity
@Table(name = "datas")
public class Data {

    @Id
    @GeneratedValue
    private Integer data_id;

    private String file_name;
    private String category;
    private LocalDateTime created_at;

}
