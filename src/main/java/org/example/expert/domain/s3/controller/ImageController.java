package org.example.expert.domain.s3.controller;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.s3.service.S3ImageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class ImageController {

    private final S3ImageService s3ImageService;

    /**
     * 이미지 s3에 등록하기
     */
    @PostMapping("/images/upload")
    public ResponseEntity<String> uploadImage (
            @RequestPart ("image") MultipartFile image
    ) {
        return ResponseEntity.ok(s3ImageService.uploadImage(image));
    }
}
