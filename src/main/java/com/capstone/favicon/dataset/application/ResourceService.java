package com.capstone.favicon.dataset.application;

import com.capstone.favicon.dataset.domain.domain.FileExtension;
import com.capstone.favicon.dataset.domain.domain.Resource;
import com.capstone.favicon.dataset.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class ResourceService {

    @Autowired
    private ResourceRepository resourceRepository;

    /**
     * datasetID로 resourceUrl 가져오기
     * @param datasetId
     * @return resourceUrl
     */
    public String getResourceUrlByDatasetId(Long datasetId) {
        Resource resource = resourceRepository.findByDatasetDatasetId(datasetId)
                .orElseThrow(() -> {
                    log.error("Resource Not Found for Dataset ID: {}", datasetId);
                    return new RuntimeException("Resource Not Found by DatasetID " + datasetId);
                });
        return resource.getResourceUrl();
    }

    /**
     * datasetID로 fileExtension 가져오기
     * @param datasetId
     * @return FileExtension
     */
    public FileExtension getFileExtensionByDatasetId(Long datasetId) {
        Optional<Resource> resource = resourceRepository.findByDatasetDatasetId(datasetId);
        return resource.map(Resource::getType).orElse(FileExtension.txt);
    }
}