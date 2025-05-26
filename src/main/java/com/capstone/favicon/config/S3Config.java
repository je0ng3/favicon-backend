package com.capstone.favicon.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class S3Config {
    protected final S3Client s3Client;
    private final String region;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public S3Config(
            @Value("${aws.s3.region}") String region,
            @Value("${aws.s3.access-key}") String accessKey,
            @Value("${aws.s3.secret-key}") String secretKey) {
        this.region = region;
        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }

    public String uploadFile(MultipartFile file, String directory) throws IOException {
        String fileName = file.getOriginalFilename();
        String fullKey = directory + "/" + fileName;  // e.g. preprocessing/파일명.csv
        String encodedFileName = URLEncoder.encode(fullKey, StandardCharsets.UTF_8).replace("+", "%20");

        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fullKey)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(putRequest, RequestBody.fromBytes(file.getBytes()));

        return "s3://" + bucketName + "/" + fullKey;
    }

    public void deleteFile(String fileUrl) {
        String key = extractKeyFromUrl(fileUrl);

        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.deleteObject(deleteRequest);
    }

    public void deleteFileByKey(String key) {
        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.deleteObject(deleteRequest);
    }

    public void moveFile(String fromKey, String toKey) {
        CopyObjectRequest copyRequest = CopyObjectRequest.builder()
                .sourceBucket(bucketName)
                .sourceKey(fromKey)
                .destinationBucket(bucketName)
                .destinationKey(toKey)
                .build();

        s3Client.copyObject(copyRequest);

        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fromKey)
                .build();

        s3Client.deleteObject(deleteRequest);
    }

    public List<String> listFilesInBucket() {
        ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .build();

        ListObjectsV2Response response = s3Client.listObjectsV2(listObjectsV2Request);

        return response.contents().stream()
                .map(object -> object.key())
                .collect(Collectors.toList());
    }

    /**
     * key에서 fileUrl 추출
     * @param fileUrl
     */
    public String extractKeyFromUrl(String fileUrl) {
        return fileUrl.substring(fileUrl.indexOf(bucketName) + bucketName.length() + 1);
    }

    /**
     * key에서 fileName 추출
     * @param key
     */
    public String extractFileNameFromKey(String key) {
        return key.substring(key.lastIndexOf("/")+1);
    }

    protected String encodeFileName(String fileName) throws UnsupportedEncodingException {
        return URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
    }

    public String generateFileUrl(String fileName) {
        return "s3://" + bucketName + "/" + fileName;  //다운로드 기능 테스트 및 경로 물어보기
        //return "https://favicon-dataset.s3.ap-northeast-2.amazonaws.com/" + fileName;
    }

    public LocalDate getLastModifiedDate(String s3Key) {
        HeadObjectRequest headRequest = HeadObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .build();

        HeadObjectResponse headObjectResponse = s3Client.headObject(headRequest);

        return headObjectResponse.lastModified()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    public String extractKeyFromAnyUrl(String fileUrl) {
        if (fileUrl.startsWith("s3://")) {
            int bucketNameEnd = fileUrl.indexOf("/", 5); // after "s3://"
            if (bucketNameEnd == -1) {
                throw new IllegalArgumentException("Invalid s3 URL: " + fileUrl);
            }
            return fileUrl.substring(bucketNameEnd + 1);
        } else if (fileUrl.contains(".amazonaws.com/")) {
            return fileUrl.substring(fileUrl.indexOf(".com/") + 5);
        } else {
            return extractKeyFromUrl(fileUrl);
        }
    }

}