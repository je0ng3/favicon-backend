package com.capstone.favicon.request.domain;

import com.capstone.favicon.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name="question")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questionId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDate createDate;
    /* private String title;
    private Integer view = 0;

    @Enumerated(EnumType.STRING)
    private AnswerStatus answerStatus = AnswerStatus.PENDING;

    @Enumerated(EnumType.STRING)
    private Category category;

    private String imageUrl;

    public enum AnswerStatus {
        PENDING, COMPLETED
    }

    public enum Category {
        GENERAL, TECHNICAL, POLICY
    } */
}

