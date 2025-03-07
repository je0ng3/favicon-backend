package com.capstone.favicon.download.controller;

import com.capstone.favicon.download.application.S3FileDownloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/dataset")
public class s3FileDownloadController {

    @Autowired
    private S3FileDownloadService s3FileDownloadService;

    @GetMapping("/download/{datasetId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long datasetId) throws IOException {

        File downloadedFile = s3FileDownloadService.downloadFile(datasetId);
        Resource fileResource = new FileSystemResource(downloadedFile);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8" + downloadedFile.getName() + "/")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(fileResource);
    }
}