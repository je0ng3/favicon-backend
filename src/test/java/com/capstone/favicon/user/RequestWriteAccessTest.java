package com.capstone.favicon.user;

import com.capstone.favicon.FaviconApplication;
import com.capstone.favicon.security.JwtUtil;
import com.capstone.favicon.user.domain.Answer;
import com.capstone.favicon.user.domain.DataRequest;
import com.capstone.favicon.user.domain.Question;
import com.capstone.favicon.user.domain.User;
import com.capstone.favicon.user.repository.AnswerRepository;
import com.capstone.favicon.user.repository.DataRequestRepository;
import com.capstone.favicon.user.repository.QuestionRepository;
import com.capstone.favicon.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 쓰기 엔드포인트는 작성자 본인 또는 관리자만 통과해야 한다. 소유권 검사는 URL 패턴이 아니라
 * 서비스 안에 있어서 SecurityConfig 만 봐서는 드러나지 않으므로, 실제 토큰으로 호출해 고정한다.
 */
@SpringBootTest(classes = FaviconApplication.class)
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(properties = {
        "ACTIVE=test",
        "JPA_DDL=create-drop",
        "spring.datasource.url=jdbc:h2:mem:writeaccessdb;MODE=PostgreSQL;NON_KEYWORDS=VIEW,RANK;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        "management.health.db.enabled=false",
        "management.health.redis.enabled=false",
        "REDIS_HOST=localhost",
        "SPRING_MAIL_USERNAME=test@example.com",
        "SPRING_MAIL_PASSWORD=test",
        "AWS_S3_BUCKET=test-bucket",
        "AWS_S3_REGION=ap-northeast-2",
        "AWS_S3_ACCESS_KEY_ID=test",
        "AWS_S3_SECRET_ACCESS_KEY=test",
        "API_KEY=test",
        "JWT_SECRET=test-jwt-secret-value-for-request-write-access-test",
        "ADMIN_MAILS=admin@example.com"
})
class RequestWriteAccessTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private AnswerRepository answerRepository;
    @Autowired
    private DataRequestRepository dataRequestRepository;

    private String ownerToken;
    private String otherToken;
    private String adminToken;
    private Long questionId;
    private Long answerId;
    private Long dataRequestId;

    private User user(String email, int role) {
        User user = new User();
        user.setEmail(email);
        user.setUsername(email);
        user.setPassword("encoded");
        user.setRole(role);
        return userRepository.save(user);
    }

    @BeforeEach
    void setUp() {
        User owner = user("owner@test.com", 0);
        User other = user("other@test.com", 0);
        User admin = user("admin@test.com", 1);

        ownerToken = jwtUtil.createAccessToken(owner);
        otherToken = jwtUtil.createAccessToken(other);
        adminToken = jwtUtil.createAccessToken(admin);

        Question question = new Question();
        question.setUser(owner);
        question.setContent("원본 질문");
        question.setCreateDate(LocalDate.now());
        questionId = questionRepository.save(question).getQuestionId();

        Answer answer = new Answer();
        answer.setQuestion(question);
        answer.setUser(owner);
        answer.setContent("원본 답변");
        answer.setCreateDate(LocalDate.now());
        answerId = answerRepository.save(answer).getAnswerId();

        DataRequest dataRequest = new DataRequest();
        dataRequest.setUser(owner);
        dataRequest.setTitle("원본 요청");
        dataRequest.setUploadDate(LocalDate.now());
        dataRequestId = dataRequestRepository.save(dataRequest).getDataRequestId();
    }

    private org.springframework.test.web.servlet.RequestBuilder updateQuestion(String token, String body) {
        return put("/request/question/{id}", questionId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body);
    }

    @Test
    void authorCanUpdateOwnQuestion() throws Exception {
        mockMvc.perform(updateQuestion(ownerToken, "{\"content\":\"내가 고침\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void otherUserCannotUpdateSomeoneElsesQuestion() throws Exception {
        mockMvc.perform(updateQuestion(otherToken, "{\"content\":\"남의 글 고치기\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanUpdateSomeoneElsesQuestion() throws Exception {
        mockMvc.perform(updateQuestion(adminToken, "{\"content\":\"관리자 수정\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void otherUserCannotDeleteSomeoneElsesAnswer() throws Exception {
        mockMvc.perform(delete("/request/answer/{id}", answerId)
                        .header("Authorization", "Bearer " + otherToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void authorCanDeleteOwnAnswer() throws Exception {
        mockMvc.perform(delete("/request/answer/{id}", answerId)
                        .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isOk());
    }

    @Test
    void blankContentIsRejected() throws Exception {
        // 빈 본문이 기존 글을 지우지 않아야 한다
        mockMvc.perform(updateQuestion(ownerToken, "{\"content\":\"   \"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void otherUserCannotDownloadSomeoneElsesRequestFile() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .get("/request/download/{id}", dataRequestId)
                        .header("Authorization", "Bearer " + otherToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void reviewEndpointIsAdminOnly() throws Exception {
        // 심사는 S3 파일 이동·삭제를 일으키므로 일반 사용자가 호출할 수 없어야 한다
        mockMvc.perform(put("/request/list/{id}/review", 1L)
                        .param("status", "REJECTED")
                        .header("Authorization", "Bearer " + otherToken))
                .andExpect(status().isForbidden());
    }
}
