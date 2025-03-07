package com.capston.favicon.user.infrastructure;

import com.capston.favicon.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<User, String> {
    User findByUsername(String username);
    User findByEmail(String email);
}

