package com.capstone.favicon.user.repository;

import com.capstone.favicon.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUserId(Long userId);
    User findByEmail(String email);
    void deleteByUserId(Long userId);

    @Query("SELECT u.userId, u.email, u.username FROM User  u WHERE u.role=0")
    List<Object[]> getAll();

    @Query("SELECT COUNT(*) FROM User  u WHERE u.role=0")
    int countAllUsers();

    @Query("SELECT COUNT(u) FROM User u " +
            "WHERE u.role=0 AND " +
            "u.createdAt >= :start AND u.createdAt < :end")
    int countUsersAt(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /** :from 이후 가입한 일반 사용자를 (연, 월) 단위로 묶어 개수를 센다. 반환: [year, month, count] */
    @Query("SELECT YEAR(u.createdAt), MONTH(u.createdAt), COUNT(u) FROM User u " +
            "WHERE u.role=0 AND u.createdAt >= :from " +
            "GROUP BY YEAR(u.createdAt), MONTH(u.createdAt)")
    List<Object[]> countMonthlyUsersSince(@Param("from") LocalDateTime from);
}

