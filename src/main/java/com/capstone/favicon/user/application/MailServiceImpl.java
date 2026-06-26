package com.capstone.favicon.user.application;

import com.capstone.favicon.user.application.service.MailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


@Service
public class MailServiceImpl implements MailService {

    private static final Logger log = LoggerFactory.getLogger(MailServiceImpl.class);

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public MailServiceImpl(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    // 별도 스레드풀에서 발송 → 요청 스레드가 SMTP 응답(수 초)을 기다리지 않는다.
    @Async("mailExecutor")
    @Override
    public void send(String to, String subject, String code) {
        try {
            Context context = new Context();
            context.setVariable("code", code);
            String htmlContent = templateEngine.process("email", context);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException | MailException e) {
            // 비동기 void 메서드라 예외를 던져도 호출자에게 전달되지 않으므로 로깅으로 남긴다.
            log.error("메일 발송 실패 - to={}, subject={}", to, subject, e);
        }
    }

}
