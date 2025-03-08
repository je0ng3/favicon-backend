package com.capstone.favicon.user.repository;


import com.capstone.favicon.user.domain.Data;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface DataRepository extends JpaRepository<Data, Integer> {

    @Query(value = """
    SELECT * FROM datas
    WHERE file_name ILIKE CONCAT('%', :keyword, '%')
    ORDER BY similarity(file_name, :keyword) DESC, created_at DESC
    """, nativeQuery = true)
    List<Data> searchByText(@Param("keyword") String keyword);

    @Query(value = """
    SELECT * FROM datas
    WHERE file_name ILIKE CONCAT('%', :keyword, '%')
    AND category = :category
    ORDER BY similarity(file_name, :keyword) DESC, created_at DESC
    """, nativeQuery = true)
    List<Data> searchWithCategory(@Param("keyword") String keyword, @Param("category") String category);
}
