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
        User user = findUser(request);
        Dataset dataset = datasetRepository.getReferenceById(dataId);
        Scrap scrap = new Scrap();
        scrap.setUser(user);
        scrap.setDataset(dataset);
        dataRepository.save(scrap);
    }

    @Override
    public void deleteScrap(HttpServletRequest request, Long scrapId) {
        User user = findUser(request);
        Scrap scrap = dataRepository.findByScrapIdAndUser(scrapId, user);
        dataRepository.delete(scrap);
    }

    @Override
    public List<Scrap> getScrap(HttpServletRequest request) {
        User user = findUser(request);
        return dataRepository.findAllByUser(user);
    }

    private User findUser(HttpServletRequest request) {
        HttpSession session = request.getSession();
        String email = session.getAttribute("email").toString();
        User user = userRepository.findByEmail(email);
        return user;
    }

}
