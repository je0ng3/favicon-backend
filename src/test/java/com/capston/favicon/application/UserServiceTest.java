package com.capston.favicon.application;

import com.capston.favicon.AppConfig;
import com.capston.favicon.application.repository.UserService;
import com.capston.favicon.domain.domain.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserServiceTest {

    UserService userService;

    @BeforeEach
    public void beforeEach(){
        AppConfig appConfig = new AppConfig();
        userService = appConfig.userService();
    }

    @Test
    void join(){
         User user = new User("test", "1234");
         this.userService.join(user);
         User findUser = this.userService.findByUsername("test");
         Assertions.assertThat(user).isEqualTo(findUser);
    }

}
