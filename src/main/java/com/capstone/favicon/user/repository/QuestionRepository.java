package com.capstone.favicon.user.repository;

import com.capstone.favicon.user.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByUser_UserId(Long userId);
}