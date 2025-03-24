package com.capstone.favicon.aws.application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class S3Service {
    protected final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public S3Service(
            @Value("${aws.s3.region}") String region,
            @Value("${aws.s3.access-key}") String accessKey,
            @Value("${aws.s3.secret-key}") String secretKey) {
        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
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