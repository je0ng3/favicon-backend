package com.capstone.favicon.dataset.application.service;

import com.capstone.favicon.dataset.domain.domain.FileExtension;

public interface ResourceService {

    /**
     * datasetID로 resourceUrl 가져오기
     * @param datasetId
     * @return resourceUrl
     */
    String getResourceUrlByDatasetId(Long datasetId);

    /**
     * datasetID로 fileExtension 가져오기
     * @param datasetId
     * @return FileExtension
     */
    FileExtension getFileExtensionByDatasetId(Long datasetId);
}
