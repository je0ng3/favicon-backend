package com.capstone.favicon.dataset.application;

import java.nio.file.Paths;

public class FilePathService {

    /**
     * 다운로드 디렉토리 경로 가져오기
     * @return 다운로드 디렉토리 경로
     */
    public static String getDownloadDir() {
        String userHome = System.getProperty("user.home");
        return Paths.get(userHome, "Downloads").toString();
    }
}
