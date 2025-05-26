package com.capstone.favicon.dataset.application;

import com.capstone.favicon.dataset.application.service.FilePathService;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;

@Service
public class FilePathServiceImpl implements FilePathService {

    /**
     * 다운로드 디렉토리 경로 가져오기
     * @return 다운로드 디렉토리 경로
     */
    @Override
    public String getDownloadDir() {
        String userHome = System.getProperty("user.home");
        return Paths.get(userHome, "Downloads").toString();
    }
}
