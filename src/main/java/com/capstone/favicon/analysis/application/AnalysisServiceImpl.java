package com.capstone.favicon.analysis.application;

import com.capstone.favicon.analysis.application.service.AnalysisService;
import com.capstone.favicon.analysis.dto.AnalysisRequestDto;
import com.capstone.favicon.analysis.dto.AnalysisResultDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.Map;

@Slf4j
@Setter
@Service
public class AnalysisServiceImpl implements AnalysisService {
    private final ObjectMapper objectMapper;

    public AnalysisServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Map<String, Object> analyze(AnalysisRequestDto requestDto) {
        try {

            // 운영체제에 맞는 파이썬 실행 명령
            String pythonCmd = System.getProperty("os.name").toLowerCase().contains("win") ? "python" : "python3";

            // Python 스크립트 경로
            String scriptPath = Paths.get("src", "main", "python", "testScript.py").toAbsolutePath().toString();

            // ProcessBuilder 설정
            ProcessBuilder pb = new ProcessBuilder(
                    pythonCmd, scriptPath,
                    requestDto.getTheme(),
                    requestDto.getRegion(),
                    requestDto.getStart().toString(),
                    requestDto.getEnd().toString()
            );

            pb.redirectErrorStream(true);

            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                log.error("Python process exited with code {}", exitCode);
                log.error("Python Output: {}", output.toString());
                throw new RuntimeException("Python process failed.");
            }

            // 결과 JSON 파싱
            Map<String, Object> result = objectMapper.readValue(output.toString(), new TypeReference<>() {});
            return result;

        } catch (Exception e) {
            log.error("파이썬 분석 중 에러 발생", e);
            throw new RuntimeException("분석 실패", e);
        }
    }

}
