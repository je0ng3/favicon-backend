package com.capstone.favicon.dataset.application.service;

import org.springframework.scheduling.annotation.Scheduled;

public interface TrendSchedulerService {
    /*@Scheduled(cron = "0 * * * * *") */ // 매 1분마다 실행(테스트용으로 하기)
    @Scheduled(cron = "0 0 0 * * *") // 매일 자정(실제 배포용으로)
    void analyzeTrends();
}
