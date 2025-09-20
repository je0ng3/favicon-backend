package com.capstone.favicon.user.application;

import com.capstone.favicon.dataset.domain.Dataset;
import com.capstone.favicon.dataset.repository.DatasetRepository;
import com.capstone.favicon.user.application.service.DataService;
import com.capstone.favicon.user.domain.Scrap;
import com.capstone.favicon.user.domain.User;
import com.capstone.favicon.user.dto.ScrapResponseDto;
import com.capstone.favicon.user.repository.DataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@RequiredArgsConstructor
@Service
public class DataServiceImpl implements DataService {

    @Autowired
    private DataRepository dataRepository;
    @Autowired
    private DatasetRepository datasetRepository;

    @Override
    public ScrapResponseDto addScrap(User user, Long dataId) {
        Dataset dataset = datasetRepository.findById(dataId).orElse(null);
        if (dataset == null) {
            throw new RuntimeException();
        }
        Scrap scrap = new Scrap();
        scrap.setUserId(user.getUserId());
        scrap.setDatasetId(dataId);
        scrap.setTitle(dataset.getTitle());
        scrap.setTheme(dataset.getDatasetTheme().getTheme());
        dataRepository.save(scrap);
        return new ScrapResponseDto(dataId, dataset.getTitle(), dataset.getDatasetTheme().getTheme());
    }

    @Override
    public void deleteScrap(User user, Long scrapId) {
        Scrap scrap = dataRepository.findByScrapIdAndUserId(scrapId, user.getUserId());
        dataRepository.delete(scrap);
    }

    @Override
    public List<Scrap> getScrap(User user) {
        return dataRepository.findAllByUserId(user.getUserId());
    }

}
