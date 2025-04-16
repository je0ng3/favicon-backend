package com.capstone.favicon.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class S3Config {
    protected final S3Client s3Client;
    private final String region;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public S3Config(
            @Value("${AWS_REGION}") String region,
            @Value("${aws.s3.access-key}") String accessKey,
            @Value("${aws.s3.secret-key}") String secretKey) {
        this.region = region;
        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }

    public String uploadFile(MultipartFile file) throws IOException {
        String fileName = /*UUID.randomUUID() + "_" + */ file.getOriginalFilename();
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");

        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(putRequest, RequestBody.fromBytes(file.getBytes()));
        //객체 url대신 s3 url로 변경하면 되는지 여쭤보기
        //return "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + encodedFileName;
        return "s3://" + bucketName + "/" + fileName;
    }

    public void deleteFile(String fileUrl) {
        String key = extractKeyFromUrl(fileUrl);

        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.deleteObject(deleteRequest);
    }

    /**
     * key에서 fileUrl 추출
     * @param fileUrl
     */
    protected String extractKeyFromUrl(String fileUrl) {
        return fileUrl.substring(fileUrl.indexOf(bucketName) + bucketName.length() + 1);
    }

    /**
     * key에서 fileName 추출
     * @param key
     */
    protected String extractFileNameFromKey(String key) {
        return key.substring(key.lastIndexOf("/")+1);
    }

    protected String encodeFileName(String fileName) throws UnsupportedEncodingException {
        return URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
    }
}