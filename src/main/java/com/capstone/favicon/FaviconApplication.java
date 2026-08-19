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
                            "com\\.capstone\\.favicon\\.dataset\\.controller\\.GPTController",
                            // 배포 이미지에 파이썬 런타임/스크립트가 없어 호출 시 무조건 실패한다
                            "com\\.capstone\\.favicon\\.dataset\\.controller\\.AnalysisController"}
        )
)
@EnableScheduling
public class FaviconApplication {

    public static void main(String[] args) {
        SpringApplication.run(FaviconApplication.class, args);
    }

}
