package com.capstone.favicon.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.MapSession;
import org.springframework.session.Session;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** 탈퇴·강제 로그아웃이 실제로 세션을 지우는지. 여기가 조용히 no-op 이 되면 지워진 계정이 계속 인증된다. */
@ExtendWith(MockitoExtension.class)
class UserSessionRegistryTest {

    @Mock
    private ObjectProvider<FindByIndexNameSessionRepository<? extends Session>> provider;
    @Mock
    private FindByIndexNameSessionRepository<Session> repository;

    private UserSessionRegistry registry() {
        return new UserSessionRegistry(provider);
    }

    private Map<String, Session> sessions(String... ids) {
        Map<String, Session> found = new LinkedHashMap<>();
        for (String id : ids) {
            found.put(id, new MapSession(id));
        }
        return found;
    }

    @Test
    void every_session_of_the_principal_is_deleted() {
        doReturn(repository).when(provider).getIfAvailable();
        when(repository.findByPrincipalName("user@test.com")).thenReturn(sessions("s1", "s2"));

        registry().expireAll("user@test.com");

        // 기기 하나만 끊고 끝나면 다른 기기는 계속 로그인 상태로 남는다
        verify(repository).deleteById("s1");
        verify(repository).deleteById("s2");
    }

    @Test
    void a_principal_without_sessions_deletes_nothing() {
        doReturn(repository).when(provider).getIfAvailable();
        when(repository.findByPrincipalName("user@test.com")).thenReturn(sessions());

        registry().expireAll("user@test.com");

        verify(repository, never()).deleteById(anyString());
    }

    @Test
    void a_missing_session_store_does_not_blow_up_the_caller() {
        // 저장소가 없으면(=indexed 설정이 아니면) 탈퇴 자체가 실패하지는 않아야 한다
        doReturn(null).when(provider).getIfAvailable();

        registry().expireAll("user@test.com");
    }
}
