package com.capstone.favicon.notice.domain;

import com.capstone.favicon.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "notice")
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long noticeId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "userId")
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 255)
    private String content;

    @Column(nullable = false)
    private LocalDate createDate = LocalDate.now();

    private LocalDate updateDate;

    @Column(nullable = false)
    private Integer view = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NoticeLabel label = NoticeLabel.일반; // 공지 유형 (일반, 업데이트, 중요)

    public void update(String title, String content, NoticeLabel label) {
        this.title = title;
        this.content = content;
        this.label = label;
        this.updateDate = LocalDate.now();
    }

    public void incrementView() {
        this.view += 1;
    }

    public enum NoticeLabel {
        일반, 업데이트, 중요
    }

}
