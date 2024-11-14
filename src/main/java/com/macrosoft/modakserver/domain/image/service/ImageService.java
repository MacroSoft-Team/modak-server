package com.macrosoft.modakserver.domain.image.service;

import com.macrosoft.modakserver.domain.image.dto.ImageResponse;
import com.macrosoft.modakserver.domain.image.entity.LogImage;
import com.macrosoft.modakserver.domain.member.entity.Member;
import java.util.List;

public interface ImageService {
    void deleteImageFromS3(String imageUrl);

    ImageResponse.ImageDetail getImageDetail(Member member, int campfirePin, Long imageId);

    ImageResponse.ImageDTO emotion(Member member, int campfirePin, Long imageId, String emotion);

    ImageResponse.ImageDTO deleteEmotion(Member member, int campfirePin, Long imageId);

    ImageResponse.ImageIds removeImages(Member member, int campfirePin, Long logId, List<Long> imageIds);

    LogImage getLogImage(Long imageId);
}