package com.capston.favicon.domain.domain;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

    @Id
    private String email;
    private String username;
    private String password;

}
