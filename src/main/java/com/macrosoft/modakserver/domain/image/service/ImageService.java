package com.macrosoft.modakserver.domain.image.service;

import com.macrosoft.modakserver.domain.image.dto.ImageResponse;
import com.macrosoft.modakserver.domain.member.entity.Member;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    ImageResponse.ImageUrl uploadImage(MultipartFile image);

    void deleteImageFromS3(String imageUrl);

    ImageResponse.ImageDetail getImageDetail(Member member, int campfirePin, Long imageId);
}