package com.capstone.favicon.dataset.application;

import com.capstone.favicon.dataset.application.service.AnalysisService;
import com.capstone.favicon.dataset.dto.AnalysisRequestDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.HashMap;
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

            String pythonCmd = "venv/bin/python3";

            // Python 스크립트 경로
            String scriptPath = Paths.get("analysis.py").toAbsolutePath().toString();


            // ProcessBuilder 설정
            ProcessBuilder pb = new ProcessBuilder(
                    pythonCmd, scriptPath,
                    requestDto.getTheme1(),
                    requestDto.getTheme2(),
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

            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            StringBuilder errorOutput = new StringBuilder();
            while ((line = errorReader.readLine()) != null) {
                errorOutput.append(line).append("\n");
            }


            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println("[Python Error Output]");
                System.err.println(errorOutput);
                System.err.println("Standard Output:");
                System.err.println(output);
                log.error("Python process exited with code {}", exitCode);
                log.error("Python Output: {}", output.toString());
                throw new RuntimeException("Python process failed.");
            }

            // 결과가 JSON인지 확인
            Map<String, Object> result = new HashMap<>();
            try {
                String fullOutput = output.toString().trim();
                int jsonStart = fullOutput.indexOf('{');

                if (jsonStart != -1) {
                    String jsonPart = fullOutput.substring(jsonStart);
                    try {
                        result = objectMapper.readValue(jsonPart, new TypeReference<>() {});
                    } catch (Exception parseEx) {
                        log.warn("유효 JSON 파싱 실패: {}", jsonPart);
                        result.put("message", "JSON 파싱 실패");
                    }
                } else {
                    log.warn("JSON 시작 구분자 '{' 없음: {}", fullOutput);
                    result.put("message", "유효한 JSON 없음");
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            // 결과 JSON 파싱
            //Map<String, Object> result = objectMapper.readValue(output.toString(), new TypeReference<>() {});

            return result;


        } catch (Exception e) {
            log.error("파이썬 분석 중 에러 발생", e);
            throw new RuntimeException("분석 실패", e);
        }
    }

}
