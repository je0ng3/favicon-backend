package com.capstone.favicon.dataset.application;

import com.capstone.favicon.config.S3Config;
import com.capstone.favicon.dataset.application.ResourceService;
import com.capstone.favicon.dataset.domain.FileExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class S3FileDownloadService extends S3Config {
    @Autowired
    private ResourceService resourceService;
    @Autowired
    private FilePathService filePathService;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public S3FileDownloadService(@Value("${aws.s3.region}") String region,
                                 @Value("${aws.s3.access-key}") String accessKey,
                                 @Value("${aws.s3.secret-key}") String secretKey) {
        super(region, accessKey, secretKey);
    }

    /**
     * S3에서 가져온 파일을 사용자의 다운로드 폴더에 저장
     * @param datasetId 데이터셋 ID
     * @return 다운로드된 파일
     * @throws IOException 파일 다운로드 중 발생할 수 있는 예외
     */
    public File downloadFile(Long datasetId) throws IOException {

        // 다운로드 경로 설정
        String downloadDir = filePathService.getDownloadDir();

        // 데이터셋 ID를 기반으로 파일 URL, 확장자 가져오기
        String fileUrl = resourceService.getResourceUrlByDatasetId(datasetId);
        FileExtension fileExtension = resourceService.getFileExtensionByDatasetId(datasetId);

        // S3에 저장된 파일 키, 이름
        String key = extractKeyFromUrl(fileUrl);
        String fileName = extractFileNameFromKey(key);
        String encodedFileName = encodeFileName(fileName);
        File file = createFileWithExtension(downloadDir, encodedFileName, fileExtension);

        // file에 내용 써서 저장
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        try (ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getObjectRequest);
             FileOutputStream fos = new FileOutputStream(file)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = s3Object.read(buffer)) != -1) {
                fos.write(buffer, 0, length);
            }
        }

        return file;
    }

    /**
     * 사용자의 다운로드 폴더에 지정된 확장자로 파일을 생성
     * @param directory 다운로드 경로
     * @param fileName 파일명
     * @param extension 파일 확장자
     * @return 생성된 파일
     * @throws IOException 파일 생성 중 발생할 수 있는 예외
     */
    private File createFileWithExtension(String directory, String fileName, FileExtension extension) throws IOException {
        File downloadDir = new File(directory);
        if (!downloadDir.exists()) {
            downloadDir.mkdirs(); // 폴더가 없으면 생성
        }
        // String encodedFileName = encodeFileName(fileName);
        return new File(downloadDir, fileName + "." + extension.name().toLowerCase());
    }
}