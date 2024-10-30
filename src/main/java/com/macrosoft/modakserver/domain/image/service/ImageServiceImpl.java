package com.macrosoft.modakserver.domain.image.service;

import com.macrosoft.modakserver.domain.image.component.S3ImageComponent;
import com.macrosoft.modakserver.domain.image.dto.ImageResponse.ImageUrl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
    private final S3ImageComponent s3ImageComponent;

    @Override
    public ImageUrl uploadImage(MultipartFile image) {
        String folderName = "dev/"; // TODO: 추후 프로필 이미지, 게시글 이미지 등 다양한 이미지 업로드를 위한 폴더명 설정
        return ImageUrl.builder()
                .imageUrl(s3ImageComponent.upload(image, folderName))
                .build();
    }

    @Override
    public void deleteImageFromS3(String imageUrl) {
        s3ImageComponent.deleteImageFromS3(imageUrl);
    }
}
