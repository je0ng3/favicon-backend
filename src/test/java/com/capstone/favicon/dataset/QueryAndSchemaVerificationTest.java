package com.capstone.favicon.dataset;

import com.capstone.favicon.dataset.domain.Dataset;
import com.capstone.favicon.dataset.domain.DatasetTheme;
import com.capstone.favicon.dataset.domain.Trend;
import com.capstone.favicon.dataset.repository.DatasetRepository;
import com.capstone.favicon.dataset.repository.TrendRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 이번 세션에서 추가/변경한 JPA 쿼리와 인덱스 매핑을 H2(PostgreSQL 호환 모드) 위에서 실제로 부팅·실행해
 * 검증한다. 컨텍스트가 뜨는 것만으로도 모든 엔티티의 @Index DDL 이 유효함이 확인된다(컬럼명이 틀리면 부팅 실패).
 *
 * H2 는 VIEW, RANK 를 예약어로 취급하므로 NON_KEYWORDS 로 풀어 PostgreSQL 과 동일하게 컬럼명으로 허용한다.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
// 앱의 커스텀 @ComponentScan(S3Config 등 @Service 포함)을 타지 않도록, 테스트 전용 최소 JPA 설정으로 격리한다.
@ContextConfiguration(classes = QueryAndSchemaVerificationTest.JpaTestConfig.class)
@TestPropertySource(properties = {
        "ACTIVE=test",
        "JPA_DDL=create-drop",
        "spring.datasource.url=jdbc:h2:mem:verifydb;MODE=PostgreSQL;NON_KEYWORDS=VIEW,RANK;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
class QueryAndSchemaVerificationTest {

    @SpringBootConfiguration
    @EnableJpaRepositories(basePackages = "com.capstone.favicon")
    @EntityScan(basePackages = "com.capstone.favicon")
    static class JpaTestConfig {
    }

    @Autowired
    private DatasetRepository datasetRepository;
    @Autowired
    private TrendRepository trendRepository;
    @Autowired
    private TestEntityManager em;

    private Dataset dataset(DatasetTheme theme, String name, Integer download) {
        Dataset d = new Dataset(theme, name, name, "org", "desc");
        d.setDownload(download);
        d.setView(0);
        return em.persist(d);
    }

    private Trend trend(Dataset dataset, int rank, LocalDate date) {
        Trend t = new Trend();
        t.setDataset(dataset);
        t.setRank(rank);
        t.setRankDate(date);
        t.setTrendStatus("유지");
        return em.persist(t);
    }

    @Test
    void findDownloadRank_uses_competition_ranking_and_treats_null_download_as_zero() {
        DatasetTheme theme = new DatasetTheme();
        theme.setTheme("교통");
        em.persist(theme);

        Dataset d100a = dataset(theme, "d100a", 100);
        Dataset d100b = dataset(theme, "d100b", 100); // 동점
        Dataset d50 = dataset(theme, "d50", 50);
        Dataset dNull = dataset(theme, "dNull", null); // download null -> 0 으로 취급
        em.flush();

        // 자기보다 download 가 '큰' 데이터셋 수 + 1 (동점은 같은 순위 공유)
        assertThat(datasetRepository.findDownloadRank(d100a.getDatasetId())).isEqualTo(1);
        assertThat(datasetRepository.findDownloadRank(d100b.getDatasetId())).isEqualTo(1);
        assertThat(datasetRepository.findDownloadRank(d50.getDatasetId())).isEqualTo(3);
        assertThat(datasetRepository.findDownloadRank(dNull.getDatasetId())).isEqualTo(4);
    }

    @Test
    void findDatasetRankPairsByDate_returns_pairs_and_excludes_trends_with_null_dataset() {
        DatasetTheme theme = new DatasetTheme();
        theme.setTheme("환경");
        em.persist(theme);

        Dataset d = dataset(theme, "d1", 10);
        LocalDate yesterday = LocalDate.now().minusDays(1);

        trend(d, 7, yesterday);
        trend(null, 99, yesterday); // dataset 이 null 인 트렌드 -> 묵시적 inner join 으로 제외되어야 함
        trend(d, 3, yesterday.minusDays(1)); // 다른 날짜 -> 제외
        em.flush();
        em.clear();

        List<Object[]> pairs = trendRepository.findDatasetRankPairsByDate(yesterday);

        assertThat(pairs).hasSize(1); // null dataset / 다른 날짜는 빠지고 1건만
        assertThat((Long) pairs.get(0)[0]).isEqualTo(d.getDatasetId());
        assertThat((Integer) pairs.get(0)[1]).isEqualTo(7);
    }
}
