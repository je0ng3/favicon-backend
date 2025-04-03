package com.capstone.favicon.user.application;

import com.capstone.favicon.dataset.domain.Dataset;
import com.capstone.favicon.dataset.repository.DatasetRepository;
import com.capstone.favicon.user.application.service.DataService;
import com.capstone.favicon.user.domain.Scrap;
import com.capstone.favicon.user.domain.User;
import com.capstone.favicon.user.repository.DataRepository;
import com.capstone.favicon.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@Service
public class DataServiceImpl implements DataService {

    @Autowired
    private DataRepository dataRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DatasetRepository datasetRepository;

    @Override
    public void addScrap(HttpServletRequest request, Long dataId) {
        HttpSession session = request.getSession();
        Long userId = (Long) session.getAttribute("id");
        Scrap scrap = new Scrap();
        scrap.setUserId(userId);
        scrap.setDatasetId(dataId);
        dataRepository.save(scrap);
    }

    @Override
    public void deleteScrap(HttpServletRequest request, Long scrapId) {
        HttpSession session = request.getSession();
        Long userId = (Long) session.getAttribute("id");
        Scrap scrap = dataRepository.findByScrapIdAndUserId(scrapId, userId);
        dataRepository.delete(scrap);
    }

    @Override
    public List<Scrap> getScrap(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Long userId = (Long) session.getAttribute("id");
        return dataRepository.findAllByUserId(userId);
    }

}
