package com.capstone.favicon.dataset.application.service;

import com.capstone.favicon.dataset.domain.FileExtension;

public interface ResourceService {
    String getResourceUrlByDatasetId(Long datasetId);

    FileExtension getFileExtensionByDatasetId(Long datasetId);
}
