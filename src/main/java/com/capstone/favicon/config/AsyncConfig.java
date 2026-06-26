package com.capstone.favicon.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 비동기 실행 설정.
 * 기본 SimpleAsyncTaskExecutor 는 호출마다 스레드를 새로 만들어(상한 없음) 트래픽이 몰리면
 * 스레드가 폭증한다. 메일 같은 I/O 작업은 경계가 있는 스레드풀 + 큐로 처리한다.
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean("mailExecutor")
    public Executor mailExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("mail-");
        // 코어/큐/최대 스레드까지 모두 포화되면 호출 스레드가 직접 실행 → 인증 메일 유실 방지(동기 폴백)
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 애플리케이션 종료 시 진행 중인 메일 발송이 끝날 때까지 대기
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();
        return executor;
    }
}
