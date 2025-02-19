package com.capston.favicon.infrastructure;

import com.capston.favicon.domain.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<User, String> {
    User findByUsername(String username);
}

