package com.macrosoft.modakserver.domain.image.service;

import static com.macrosoft.modakserver.domain.image.exception.ImageErrorCode.LOG_IMAGE_NOT_FOUND;
import static com.macrosoft.modakserver.domain.image.exception.ImageErrorCode.LOG_IMAGE_NOT_IN_CAMPFIRE;

import com.macrosoft.modakserver.domain.campfire.entity.Campfire;
import com.macrosoft.modakserver.domain.campfire.service.CampfireService;
import com.macrosoft.modakserver.domain.image.component.S3ImageComponent;
import com.macrosoft.modakserver.domain.image.dto.ImageResponse;
import com.macrosoft.modakserver.domain.image.dto.ImageResponse.ImageUrl;
import com.macrosoft.modakserver.domain.image.entity.LogImage;
import com.macrosoft.modakserver.domain.image.repository.LogImageRepository;
import com.macrosoft.modakserver.domain.log.repository.LogRepository;
import com.macrosoft.modakserver.domain.log.service.LogService;
import com.macrosoft.modakserver.domain.member.entity.Member;
import com.macrosoft.modakserver.domain.member.service.MemberService;
import com.macrosoft.modakserver.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
    private final S3ImageComponent s3ImageComponent;
    private final LogService logService;
    private final MemberService memberService;
    private final CampfireService campfireService;
    private final LogRepository logRepository;
    private final LogImageRepository logImageRepository;

    @Override
    public ImageUrl uploadImage(MultipartFile image) {
        String folderName = "dev/"; // TODO: 추후 프로필 이미지, 게시글 이미지 등 다양한 이미지 업로드를 위한 폴더명 설정
        return new ImageUrl(s3ImageComponent.upload(image, folderName));
    }

    @Override
    public void deleteImageFromS3(String imageUrl) {
        s3ImageComponent.deleteImageFromS3(imageUrl);
    }

    @Override
    public ImageResponse.ImageDetail getImageDetail(Member member, int campfirePin, Long imageId) {
        Member memberInDB = memberService.getMemberInDB(member);
        Campfire campfire = campfireService.findCampfireByPin(campfirePin);
        campfireService.validateMemberInCampfire(memberInDB, campfire);
        LogImage logImage = getLogImage(imageId);
        validateLogImageInCampfire(campfire, logImage);
        return ImageResponse.ImageDetail.of(logImage);
    }

    private LogImage getLogImage(Long imageId) {
        return logImageRepository.findById(imageId)
                .orElseThrow(() -> new CustomException(LOG_IMAGE_NOT_FOUND));
    }

    private void validateLogImageInCampfire(Campfire campfire, LogImage logImage) {
        if (!logImage.getLog().getCampfire().equals(campfire)) {
            throw new CustomException(LOG_IMAGE_NOT_IN_CAMPFIRE);
        }
    }
}
