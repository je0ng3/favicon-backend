package com.capstone.favicon.user.repository;

import com.capstone.favicon.user.domain.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findByQuestion_User_UserId(Long questionId);
}