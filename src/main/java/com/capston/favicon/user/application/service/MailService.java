package com.capston.favicon.user.application.service;

public interface MailService {
    void send(String to, String subject, String text);
}
