package com.capston.favicon.application.repository;

public interface MailService {
    void send(String to, String subject, String text);
}
