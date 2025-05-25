package com.capstone.favicon.user.repository;

import com.capstone.favicon.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUserId(Long userId);
    User findByEmail(String email);

    @Query("SELECT COUNT(*) FROM User  u WHERE u.role=0")
    int countAllUsers();

    @Query("SELECT COUNT(u) FROM User u " +
            "WHERE u.role=0 AND " +
            "u.createdAt >= :start AND u.createdAt < :end")
    int countUsersAt(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}

