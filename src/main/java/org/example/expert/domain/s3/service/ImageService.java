package org.example.expert.domain.s3.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    //이미지 업로드
    String uploadImage(MultipartFile image);
}
