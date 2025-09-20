package com.capstone.favicon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan(
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.REGEX,
                pattern = {"com\\.capstone\\.favicon\\.aws\\..*",
                            "com\\.capstone\\.favicon\\.dataset\\.controller\\.GPTController"}
        )
)
@EnableScheduling
public class FaviconApplication {

    public static void main(String[] args) {
        SpringApplication.run(FaviconApplication.class, args);
    }

}
