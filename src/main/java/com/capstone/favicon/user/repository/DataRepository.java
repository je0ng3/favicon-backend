package com.capstone.favicon.user.repository;

import com.capstone.favicon.user.domain.Scrap;
import com.capstone.favicon.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataRepository extends JpaRepository<Scrap, Long> {
    List<Scrap> findAllByUserId(Long userId);
    Scrap findByScrapIdAndUserId(Long scrapId, Long userId);
}
