package com.capstone.favicon.aws;

import com.capstone.favicon.config.S3Config;
import com.capstone.favicon.dataset.domain.Dataset;
import com.capstone.favicon.dataset.domain.DatasetTheme;
import com.capstone.favicon.dataset.domain.Resource;
import com.capstone.favicon.dataset.repository.DatasetRepository;
import com.capstone.favicon.dataset.repository.DatasetThemeRepository;
import com.capstone.favicon.aws.MetadataParser.DatasetMetadata;
import com.capstone.favicon.dataset.repository.ResourceRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.capstone.favicon.dataset.domain.FileExtension;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class S3MetadataSyncService {
    private final S3Config s3Config;
    private final DatasetRepository datasetRepository;
    private final DatasetThemeRepository datasetThemeRepository;
    private final ResourceRepository resourceRepository;

    public S3MetadataSyncService(S3Config s3Config, DatasetRepository datasetRepository, DatasetThemeRepository datasetThemeRepository, ResourceRepository resourceRepository) {
        this.s3Config = s3Config;
        this.datasetRepository = datasetRepository;
        this.datasetThemeRepository = datasetThemeRepository;
        this.resourceRepository = resourceRepository;
    }

    @Transactional
    @Scheduled(fixedRate = 600000000)
    public void syncS3FilesToDB() {
        List<String> fileNames = s3Config.listFilesInBucket();
        List<DatasetTheme> datasetThemes = datasetThemeRepository.findAll();

        for (String fileName : fileNames) {
            try {
                System.out.println("=== [디버깅] S3에서 가져온 전체 파일 경로: " + fileName);
                DatasetMetadata metadata = MetadataParser.extractMetadata(fileName, datasetThemes);

                Dataset datasetToUse = datasetRepository.findByS3Key(fileName).orElse(null);

                if (datasetToUse == null) {
                    Dataset dataset = new Dataset(
                            datasetThemes.stream().filter(theme -> theme.getDatasetThemeId().equals(metadata.getDatasetThemeId())).findFirst().orElse(null),
                            metadata.getName(), metadata.getTitle(), metadata.getOrganization(), metadata.getDescription()
                    );

                    dataset.setUpdateDate(LocalDate.now());
                    dataset.setDownload(0);
                    dataset.setView(0);
                    dataset.setCreatedDate(LocalDate.now());
                    dataset.setS3Key(fileName);

                    datasetRepository.save(dataset);
                    datasetToUse = dataset;
                    System.out.println("새로운 Dataset 테이블 추가됨: " + metadata.getName());

                    String rawFileName = fileName.substring(fileName.lastIndexOf("/") + 1);
                    String fileUrl = s3Config.generateFileUrl(fileName);
                    System.out.println("[디버깅] Resource 저장 시도 - dataset: " + datasetToUse.getName() + ", resourceName: " + rawFileName + ", url: " + fileUrl);
                    Resource newResource = new Resource(datasetToUse, rawFileName, FileExtension.CSV, fileUrl);
                    resourceRepository.save(newResource);
                    System.out.println("[디버깅] Resource 저장 완료: " + rawFileName);
                } else {
                    
                    String rawFileName = fileName.substring(fileName.lastIndexOf("/") + 1);
                    Optional<Resource> optionalResourceFromDB = resourceRepository.findByDatasetAndResourceName(datasetToUse, rawFileName);

                    if (optionalResourceFromDB.isPresent()) {
                        System.out.println("[정보] Resource 이미 존재함: " + rawFileName);
                    } else {
                        String fileUrl = s3Config.generateFileUrl(fileName);
                        Resource newResource = new Resource(datasetToUse, rawFileName, FileExtension.CSV, fileUrl);
                        resourceRepository.save(newResource);
                        System.out.println("[정보] 기존 Dataset에 새로운 Resource 저장: " + rawFileName);
                    }
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
