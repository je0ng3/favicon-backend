package com.capstone.favicon.aws;

import com.capstone.favicon.config.S3Config;
import com.capstone.favicon.dataset.domain.Dataset;
import com.capstone.favicon.dataset.domain.DatasetTheme;
import com.capstone.favicon.dataset.repository.DatasetRepository;
import com.capstone.favicon.dataset.repository.DatasetThemeRepository;
import com.capstone.favicon.aws.MetadataParser.DatasetMetadata;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.List;

@Service
public class S3MetadataSyncService {
    private final S3Config s3Config;
    private final DatasetRepository datasetRepository;
    private final DatasetThemeRepository datasetThemeRepository;

    public S3MetadataSyncService(S3Config s3Config, DatasetRepository datasetRepository, DatasetThemeRepository datasetThemeRepository) {
        this.s3Config = s3Config;
        this.datasetRepository = datasetRepository;
        this.datasetThemeRepository = datasetThemeRepository;
    }

    @Scheduled(fixedRate = 60000)
    public void syncS3FilesToDB() {
        List<String> fileNames = s3Config.listFilesInBucket();
        List<DatasetTheme> datasetThemes = datasetThemeRepository.findAll();

        for (String fileName : fileNames) {
            try {
                DatasetMetadata metadata = MetadataParser.extractMetadata(fileName, datasetThemes);

                Dataset existingDataset = datasetRepository
                        .findByDatasetThemeAndNameAndOrganization(
                                datasetThemes.stream().filter(theme -> theme.getDatasetThemeId().equals(metadata.getDatasetThemeId())).findFirst().orElse(null),
                                metadata.getName(), metadata.getOrganization())
                        .orElse(null);

                if (existingDataset == null) {
                    Dataset dataset = new Dataset(
                            datasetThemes.stream().filter(theme -> theme.getDatasetThemeId().equals(metadata.getDatasetThemeId())).findFirst().orElse(null),
                            metadata.getName(), metadata.getTitle(), metadata.getOrganization()
                    );

                    dataset.setUpdateDate(LocalDate.now());  // 현재 날짜로 설정
                    dataset.setDownload(0);

                    datasetRepository.save(dataset);
                    System.out.println("새로운 데이터셋 추가됨: " + metadata.getName());
                }
            } catch (Exception e) {
                System.err.println("메타데이터 추출 중 오류 발생: " + fileName);
                e.printStackTrace();
            }
        }

        try {
            ProcessBuilder processBuilder = new ProcessBuilder("venv/bin/python3", "s3_rds.py");
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("[Python] " + line);
                }
            }

            int exitCode = process.waitFor();
            System.out.println("Python 스크립트 종료 코드: " + exitCode);
        } catch (Exception e) {
            System.err.println("Python 스크립트 실행 중 오류 발생");
            e.printStackTrace();
        }
    }
}
