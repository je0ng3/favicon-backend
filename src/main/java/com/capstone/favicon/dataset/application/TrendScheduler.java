package com.capstone.favicon.dataset.application;

import com.capstone.favicon.dataset.domain.Dataset;
import com.capstone.favicon.dataset.domain.Trend;
import com.capstone.favicon.dataset.repository.DatasetRepository;
import com.capstone.favicon.dataset.repository.TrendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TrendScheduler {

    private final DatasetRepository datasetRepository;
    private final TrendRepository trendRepository;

    /*@Scheduled(cron = "0 * * * * *") */ // 매 1분마다 실행
    @Scheduled(cron = "0 0 0 * * *") // 매일 자정
    public void analyzeTrends() {
        List<Dataset> datasets = datasetRepository.findAllByOrderByDownloadDesc();
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        for (int i = 0; i < datasets.size(); i++) {
            Dataset dataset = datasets.get(i);
            int currentRank = i + 1;

            Optional<Trend> previousTrendOpt = trendRepository.findByDatasetIdAndDate(dataset.getDatasetId(), yesterday);

            String status = "유지";
            if (previousTrendOpt.isPresent()) {
                int previousRank = previousTrendOpt.get().getRank();
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

            trendRepository.save(trend);
        }
    }
}
