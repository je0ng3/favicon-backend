package com.capstone.favicon.notice.application;

import com.capstone.favicon.notice.domain.Notice;
import com.capstone.favicon.notice.dto.NoticeRequestDto;
import com.capstone.favicon.notice.dto.NoticeResponseDto;
import com.capstone.favicon.notice.repository.NoticeRepository;
import com.capstone.favicon.user.domain.User;
import com.capstone.favicon.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;

    public NoticeService(NoticeRepository noticeRepository, UserRepository userRepository) {
        this.noticeRepository = noticeRepository;
        this.userRepository = userRepository;
    }

    public void createNotice(Long userId, NoticeRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        Notice notice = new Notice();
        notice.setUser(user);
        notice.setTitle(request.getTitle());
        notice.setContent(request.getContent());
        notice.setLabel(request.getLabel());
        notice.setUpdateDate(LocalDate.now());

        noticeRepository.save(notice);
    }

    public void updateNotice(Long noticeId, NoticeRequestDto request) {
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new RuntimeException("공지사항을 찾을 수 없습니다."));
        notice.setTitle(request.getTitle());
        notice.setContent(request.getContent());
        notice.setLabel(request.getLabel());
        notice.setUpdateDate(LocalDate.now());
        noticeRepository.save(notice);
    }

    public void deleteNotice(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new RuntimeException("공지사항을 찾을 수 없습니다."));
        noticeRepository.delete(notice);
    }

    public List<NoticeResponseDto> getAllNotices() {
        List<Notice> notices = noticeRepository.findAll();
        return notices.stream()
                .map(notice -> new NoticeResponseDto(
                        notice.getNoticeId(),
                        notice.getTitle(),
                        notice.getContent(),
                        notice.getCreateDate().toString(),
                        notice.getView(),
                        notice.getLabel().name()
                ))
                .collect(Collectors.toList());
    }

    public NoticeResponseDto getNoticeById(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new RuntimeException("공지사항을 찾을 수 없습니다."));

        return new NoticeResponseDto(
                notice.getNoticeId(),
                notice.getTitle(),
                notice.getContent(),
                notice.getCreateDate().toString(),
                notice.getView(),
                notice.getLabel().name()
        );

    }

    public Notice getNotice(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 공지가 없습니다. ID: " + noticeId));

        notice.incrementView();

        return noticeRepository.save(notice);
    }

}