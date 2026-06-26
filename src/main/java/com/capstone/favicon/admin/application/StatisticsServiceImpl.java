package com.capstone.favicon.admin.application;


import com.capstone.favicon.admin.application.service.StatisticsService;
import com.capstone.favicon.admin.dto.MonthlyCountDto;
import com.capstone.favicon.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public List<MonthlyCountDto> getUserOverview() {
        // 최근 6개월(당월 포함) 시작 시점부터 가입한 사용자를 DB에서 (연,월)별로 집계한다.
        LocalDateTime from = LocalDate.now().withDayOfMonth(1).minusMonths(5).atStartOfDay();
        Map<YearMonth, Long> counts = new HashMap<>();
        for (Object[] row : userRepository.countMonthlyUsersSince(from)) {
            YearMonth ym = YearMonth.of(((Number) row[0]).intValue(), ((Number) row[1]).intValue());
            counts.put(ym, ((Number) row[2]).longValue());
        }

        // 당월부터 과거 순으로 6개 항목을 채운다(빠진 달은 0).
        List<MonthlyCountDto> result = new ArrayList<>();
        YearMonth current = YearMonth.now();
        for (int i = 0; i < 6; i++) {
            YearMonth month = current.minusMonths(i);
            result.add(new MonthlyCountDto(month.getMonthValue(), counts.getOrDefault(month, 0L).intValue()));
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getAllUsers() {
        return userRepository.getAll();
    }

}
