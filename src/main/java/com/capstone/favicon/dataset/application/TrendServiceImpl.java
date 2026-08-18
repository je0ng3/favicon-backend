package com.capstone.favicon.dataset.application;

import com.capstone.favicon.config.exception.ResourceNotFoundException;
import com.capstone.favicon.dataset.application.service.TrendService;
import com.capstone.favicon.dataset.domain.Trend;
import com.capstone.favicon.dataset.repository.DatasetRepository;
import com.capstone.favicon.dataset.repository.TrendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TrendServiceImpl implements TrendService {

    private final TrendRepository trendRepository;
    private final DatasetRepository datasetRepository;

    @Override
    public List<Trend> getTrendsByDate(LocalDate date) {
        List<Trend> trends = trendRepository.findAllByRankDate(date);
        if (trends.isEmpty()) {
            throw new ResourceNotFoundException("찾을 수 없음");
        }
        return trends;
    }

    @Override
    public List<Trend> getTrendsByDatasetId(Long datasetId, LocalDate startDate, LocalDate endDate) {
        List<Trend> trends = trendRepository.findByDatasetIdAndDateRange(datasetId, startDate, endDate);
        if (trends.isEmpty()) {
            throw new ResourceNotFoundException("찾을 수 없음");
        }
        return trends;
    }

    @Override
    public long getCurrentRank(Long datasetId) {
        if (!datasetRepository.existsById(datasetId)) {
            throw new ResourceNotFoundException("찾을 수 없음");
        }
        return datasetRepository.findDownloadRank(datasetId); // 전체 로드 없이 DB 에서 순위 계산
    }
}
