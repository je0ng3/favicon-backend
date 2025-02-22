package com.capstone.favicon.domain.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ID 자동 증가
    private Long id;

    @Column(nullable = false, unique = true) // username 중복 방지
    private String username;

    @Column(nullable = false)
    private String password;

}
