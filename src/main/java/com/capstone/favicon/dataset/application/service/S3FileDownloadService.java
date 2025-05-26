package com.capstone.favicon.dataset.application.service;

import java.io.File;
import java.io.IOException;

public interface S3FileDownloadService {
    File downloadFile(Long datasetId) throws IOException;

    File downloadFileFromDataRequest(Long dataRequestId) throws IOException;
}
