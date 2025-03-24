package com.capstone.favicon.faq.repository;

import com.capstone.favicon.faq.domain.FAQ;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FAQRepository extends JpaRepository<FAQ, Long> {
}