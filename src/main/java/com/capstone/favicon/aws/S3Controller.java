package com.capstone.favicon.aws;

import com.capstone.favicon.config.S3Config;
import com.capstone.favicon.dataset.domain.Dataset;
import com.capstone.favicon.dataset.domain.FileExtension;
import com.capstone.favicon.dataset.repository.DatasetRepository;
import com.capstone.favicon.dataset.domain.DatasetTheme;
import com.capstone.favicon.dataset.domain.Resource;
import com.capstone.favicon.dataset.repository.DatasetThemeRepository;
import com.capstone.favicon.dataset.repository.ResourceRepository;
import com.capstone.favicon.aws.MetadataParser.DatasetMetadata;
import com.capstone.favicon.config.APIResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/s3")
public class S3Controller {

    private final S3Config s3Config;
    private final DatasetRepository datasetRepository;
    private final DatasetThemeRepository datasetThemeRepository;
    private final ResourceRepository resourceRepository;

    public S3Controller(@Qualifier("s3Config") S3Config s3Config, DatasetRepository datasetRepository,
                        DatasetThemeRepository datasetThemeRepository, ResourceRepository resourceRepository) {
        this.s3Config = s3Config;
        this.datasetRepository = datasetRepository;
        this.datasetThemeRepository = datasetThemeRepository;
        this.resourceRepository = resourceRepository;
    }

    @PostMapping("/upload")
    public ResponseEntity<APIResponse<String>> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty() || file.getOriginalFilename() == null || file.getOriginalFilename().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(APIResponse.errorAPI("파일 이름이 올바르지 않습니다."));
            }

            String originalFileName = file.getOriginalFilename().trim();
            String directory = "preprocessing";
            String fileUrl = s3Config.uploadFile(file, directory);

            List<DatasetTheme> datasetThemes = datasetThemeRepository.findAll();
            String s3FileName = directory + "/" + originalFileName;
            DatasetMetadata metadata = MetadataParser.extractMetadata(s3FileName, datasetThemes);

            DatasetTheme datasetTheme = datasetThemes.stream()
                    .filter(theme -> theme.getDatasetThemeId().equals(metadata.getDatasetThemeId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("해당 dataset_theme_id가 존재하지 않습니다: " + metadata.getDatasetThemeId()));

            Dataset dataset = datasetRepository
                    .findByDatasetThemeAndNameAndOrganization(datasetTheme, metadata.getName(), metadata.getOrganization())
                    .orElseGet(() -> {
                        LocalDate lastModified = s3Config.getLastModifiedDate(s3FileName);
                        return datasetRepository.save(new Dataset(
                                datasetTheme,
                                metadata.getName(),
                                metadata.getTitle(),
                                metadata.getOrganization(),
                                metadata.getDescription(),
                                s3FileName,
                                LocalDate.now(),
                                lastModified,
                                0,
                                0
                        ));
                    });

            FileExtension type = FileExtension.valueOf(metadata.getType());
            Resource resource = new Resource(dataset, originalFileName, type, fileUrl);
            resourceRepository.save(resource);

            return ResponseEntity.ok(APIResponse.successAPI("파일이 업로드되었습니다", fileUrl));

        } catch (IllegalArgumentException | IOException e) {
            return ResponseEntity.badRequest().body(APIResponse.errorAPI(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(APIResponse.errorAPI("서버 오류: " + e.getMessage()));
        }
    }

    @Transactional
    @DeleteMapping("/delete/{resourceId}")
    public ResponseEntity<APIResponse<String>> deleteFile(@PathVariable Long resourceId) {
        try {
            Resource resource = resourceRepository.findById(resourceId)
                    .orElseThrow(() -> new IllegalArgumentException("Resource를 찾을 수 없습니다: " + resourceId));

            Dataset dataset = resource.getDataset();

            s3Config.deleteFile(resource.getResourceUrl());
            dataset.setResource(null); // 연결 해제

            resourceRepository.delete(resource);
            resourceRepository.flush();

            if (datasetRepository.existsById(dataset.getDatasetId()) && dataset.getResource() == null) {
                datasetRepository.delete(dataset);
                datasetRepository.flush();
            }

            return ResponseEntity.ok(APIResponse.successAPI("파일이 삭제되었습니다", null));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(APIResponse.errorAPI(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(APIResponse.errorAPI("서버 오류: " + e.getMessage()));
        }
    }
}