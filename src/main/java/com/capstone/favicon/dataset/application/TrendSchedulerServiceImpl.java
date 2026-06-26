package com.capstone.favicon.dataset.application;

import com.capstone.favicon.dataset.application.service.TrendSchedulerService;
import com.capstone.favicon.dataset.domain.Dataset;
import com.capstone.favicon.dataset.domain.Trend;
import com.capstone.favicon.dataset.repository.DatasetRepository;
import com.capstone.favicon.dataset.repository.TrendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TrendSchedulerServiceImpl implements TrendSchedulerService {

    private final DatasetRepository datasetRepository;
    private final TrendRepository trendRepository;

    /*@Scheduled(cron = "0 * * * * *") */ // 매 1분마다 실행(테스트용으로 하기)
    @Scheduled(cron = "0 0 0 * * *") // 매일 자정(실제 배포용으로)
    @Transactional
    @Override
    public void analyzeTrends() {
        List<Dataset> datasets = datasetRepository.findAllByOrderByDownloadDesc();
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        // 어제 순위를 (datasetId -> rank) 한 번의 projection 쿼리로 모두 가져와 인덱싱 (N+1 제거)
        Map<Long, Integer> previousRanks = trendRepository.findDatasetRankPairsByDate(yesterday).stream()
                .collect(Collectors.toMap(r -> (Long) r[0], r -> (Integer) r[1], (a, b) -> a));

        List<Trend> trends = new ArrayList<>(datasets.size());
        for (int i = 0; i < datasets.size(); i++) {
            Dataset dataset = datasets.get(i);
            int currentRank = i + 1;

            Integer previousRank = previousRanks.get(dataset.getDatasetId()); // O(1) 조회

            String status = "유지";
            if (previousRank != null) {
                if (currentRank < previousRank) {
                    status = "상승";
                } else if (currentRank > previousRank) {
                    status = "하락";
                }
            }

            Trend trend = new Trend();
            trend.setDataset(dataset);
            trend.setRank(currentRank);
            trend.setTrendStatus(status);
            trend.setRankDate(today);
            trends.add(trend);
        }

        trendRepository.saveAll(trends); // 배치 저장
    }
}
