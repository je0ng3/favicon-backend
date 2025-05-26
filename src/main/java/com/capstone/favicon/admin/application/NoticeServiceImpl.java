package com.capstone.favicon.admin.application;

import com.capstone.favicon.admin.application.service.FAQService;
import com.capstone.favicon.admin.application.service.NoticeService;
import com.capstone.favicon.admin.domain.Notice;
import com.capstone.favicon.admin.dto.NoticeRequestDto;
import com.capstone.favicon.admin.dto.NoticeResponseDto;
import com.capstone.favicon.admin.repository.NoticeRepository;
import com.capstone.favicon.admin.domain.FAQ;
import com.capstone.favicon.admin.dto.FAQRequestDto;
import com.capstone.favicon.admin.dto.FAQResponseDto;
import com.capstone.favicon.admin.repository.FAQRepository;
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
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;

    @Override
    public User getAdminUserFromSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new RuntimeException("세션이 존재하지 않습니다.");
        }

        Long id = (Long) session.getAttribute("id");
        if (id == null) {
            throw new RuntimeException("세션에 로그인 정보가 없습니다.");
        }

        User user = userRepository.findByUserId(id);
        if (user == null || user.getRole() != 1) {
            throw new RuntimeException("이 기능은 관리자만 접근 가능합니다.");
        }
        return user;
    }

    @Override
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

    @Override
    public void updateNotice(Long noticeId, NoticeRequestDto request) {
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new RuntimeException("공지사항을 찾을 수 없습니다."));
        notice.setTitle(request.getTitle());
        notice.setContent(request.getContent());
        notice.setLabel(request.getLabel());
        notice.setUpdateDate(LocalDate.now());
        noticeRepository.save(notice);
    }

    @Override
    public void deleteNotice(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new RuntimeException("공지사항을 찾을 수 없습니다."));
        noticeRepository.delete(notice);
    }

    @Override
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

    @Override
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

    @Override
    public Notice getNotice(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 공지가 없습니다. ID: " + noticeId));

        notice.incrementView();

        return noticeRepository.save(notice);
    }

    @Service
    @RequiredArgsConstructor
    public static class FAQServiceImpl implements FAQService {

        private final FAQRepository faqRepository;
        private final UserRepository userRepository;

        @Override
        public void createFAQ(Long userId, FAQRequestDto request) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
            FAQ faq = new FAQ();
            faq.setUser(user);
            faq.setCategory(request.getCategory());
            faq.setQuestion(request.getQuestion());
            faq.setAnswer(request.getAnswer());

            faqRepository.save(faq);
        }

        @Override
        public void updateFAQ(Long faqId, FAQRequestDto request) {
            FAQ faq = faqRepository.findById(faqId)
                    .orElseThrow(() -> new RuntimeException("FAQ를 찾을 수 없습니다."));

            faq.setCategory(request.getCategory());
            faq.setQuestion(request.getQuestion());
            faq.setAnswer(request.getAnswer());

            faqRepository.save(faq);
        }

        @Override
        public void deleteFAQ(Long faqId) {
            FAQ faq = faqRepository.findById(faqId)
                    .orElseThrow(() -> new RuntimeException("FAQ를 찾을 수 없습니다."));
            faqRepository.delete(faq);
        }

        public User getAdminUserFromSession(HttpServletRequest request) {
            HttpSession session = request.getSession();
            Long id = (Long) session.getAttribute("id");
            User user = userRepository.findByUserId(id);
            if (user == null || user.getRole() != 1) {
                throw new RuntimeException("이 기능은 관리자만 접근 가능합니다.");
            }
            return user;
        }

        @Override
        public List<FAQResponseDto> getAllFAQs() {
            List<FAQ> faqs = faqRepository.findAll();
            return faqs.stream().map(FAQResponseDto::new).collect(Collectors.toList());
        }

        @Override
        public FAQResponseDto getFAQById(Long faqId) {
            FAQ faq = faqRepository.findById(faqId)
                    .orElseThrow(() -> new RuntimeException("FAQ를 찾을 수 없습니다."));
            return new FAQResponseDto(faq);
        }

    }
}
