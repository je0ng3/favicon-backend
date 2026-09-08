package com.capstone.favicon.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.stereotype.Component;

/** 사용자 단위 세션 강제 만료. 인덱스 조회는 principal name(= email) 기준. */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserSessionRegistry {

    // 세션 저장소를 끄고 뜨는 테스트 컨텍스트에서는 빈이 없다
    private final ObjectProvider<FindByIndexNameSessionRepository<? extends Session>> sessionRepository;

    public void expireAll(String email) {
        FindByIndexNameSessionRepository<? extends Session> repository = sessionRepository.getIfAvailable();
        if (repository == null) {
            // repository-type=indexed 가 아니면 여기로 온다. 탈퇴한 계정의 세션이 그대로 살아남는다
            log.warn("세션 인덱스 저장소가 없어 {} 의 세션을 만료시키지 못했다", email);
            return;
        }
        repository.findByPrincipalName(email).keySet().forEach(repository::deleteById);
    }
}
