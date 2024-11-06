package com.favicon.capstone.user.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.favicon.capstone.user.domain.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    //Optional<User> findByIdAndPassword(String id, String password);

    Optional<User> findById(String id);

    // 추가적인 쿼리 메서드가 필요하면 여기에 작성하세요.
}

