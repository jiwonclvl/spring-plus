package org.example.expert.domain.s3.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3ImageService implements ImageService{
    private final S3Client s3Client;

    @Value("${spring.cloud.aws.s3.bucketName}")
    private String bucket;

    /**
     *
     * @param image (@RequestPart로 받아온 이미지 파일)
     * @return imageName (UUID.png 형태)
     */
    @Override
    public String uploadImage(MultipartFile image) {
        if (image.isEmpty()) {
            throw new IllegalArgumentException("이미지가 존재하지 않는다.");
        }

        //고유의 UUID 생성
        String imageName = UUID.randomUUID() + "_" + image.getOriginalFilename();

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(imageName)
                    .contentType(image.getContentType())
                    .contentLength(image.getSize())
                    .build();

            //S3 업로드
            PutObjectResponse response = s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromBytes(image.getBytes())
            );

            if (response.sdkHttpResponse().isSuccessful()) {
                return imageName;
            } else {
                throw new RuntimeException("S3 업로드 실패: " + response.sdkHttpResponse().statusText().orElse("Unknown error"));
            }
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 중 오류 발생", e);
        }
    }
}
