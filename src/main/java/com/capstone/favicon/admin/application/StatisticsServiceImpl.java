package com.capstone.favicon.admin.application;


import com.capstone.favicon.admin.application.service.StatisticsService;
import com.capstone.favicon.admin.dto.MonthlyCountDto;
import com.capstone.favicon.user.domain.User;
import com.capstone.favicon.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public Map<String, Object> getUserCount() {
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime start = today.minusMonths(1).withDayOfMonth(1);
        LocalDateTime end = start.plusMonths(1);

        int previousCount = userRepository.countUsersAt(start, end);
        int currentCount = userRepository.countUsersAt(end, today.plusDays(1));

        double rate = 0.0;
        if (previousCount > 0) {
            rate = ((double) (currentCount-previousCount) / previousCount) *100.0;
            rate = Math.round(rate*10.0)/10.0;
        } else {
            rate = 100.0;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("total", userRepository.countAllUsers());
        result.put("rate", rate);
        return result;
    }

    @Override
    public List<MonthlyCountDto> getUserOverview() {
        LocalDateTime end = LocalDateTime.now().plusMonths(1).withDayOfMonth(1);
        List<MonthlyCountDto> result = new ArrayList<>();
        for (int i = 0; i<6; i++) {
            LocalDateTime start = end.minusMonths(1);
            int month = start.getMonthValue();
            int count = userRepository.countUsersAt(start, end);
            result.add(new MonthlyCountDto(month, count));
            end = start;
        }
        return result;
    }

    @Override
    public List<Object[]> getAllUsers() {
        return userRepository.getAll();
    }

}
