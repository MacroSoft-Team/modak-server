package com.macrosoft.modakserver.domain.image.service;

import com.macrosoft.modakserver.domain.image.component.S3ImageComponent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final S3ImageComponent s3ImageComponent;

    @Override
    public void deleteImageFromS3(String imageName) {
        s3ImageComponent.deleteImageFromS3ByImageName(imageName);
    }
}
