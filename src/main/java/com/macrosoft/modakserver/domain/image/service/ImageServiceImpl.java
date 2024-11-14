package com.macrosoft.modakserver.domain.image.service;

import static com.macrosoft.modakserver.domain.image.exception.ImageErrorCode.EMOTE_EMPTY;
import static com.macrosoft.modakserver.domain.image.exception.ImageErrorCode.EMOTE_TOO_LONG;
import static com.macrosoft.modakserver.domain.image.exception.ImageErrorCode.EMOTION_NOT_FOUND;
import static com.macrosoft.modakserver.domain.image.exception.ImageErrorCode.EMOTION_NOT_UPLOAD_USER;
import static com.macrosoft.modakserver.domain.image.exception.ImageErrorCode.LOG_IMAGE_NOT_FOUND;
import static com.macrosoft.modakserver.domain.image.exception.ImageErrorCode.LOG_IMAGE_NOT_IN_CAMPFIRE;

import com.macrosoft.modakserver.domain.campfire.entity.Campfire;
import com.macrosoft.modakserver.domain.campfire.service.CampfireService;
import com.macrosoft.modakserver.domain.image.component.S3ImageComponent;
import com.macrosoft.modakserver.domain.image.dto.ImageResponse;
import com.macrosoft.modakserver.domain.image.dto.ImageResponse.ImageDTO;
import com.macrosoft.modakserver.domain.image.dto.ImageResponse.ImageUrl;
import com.macrosoft.modakserver.domain.image.entity.Emotion;
import com.macrosoft.modakserver.domain.image.entity.LogImage;
import com.macrosoft.modakserver.domain.image.repository.EmotionRepository;
import com.macrosoft.modakserver.domain.image.repository.LogImageRepository;
import com.macrosoft.modakserver.domain.log.repository.LogRepository;
import com.macrosoft.modakserver.domain.log.service.LogService;
import com.macrosoft.modakserver.domain.member.entity.Member;
import com.macrosoft.modakserver.domain.member.service.MemberService;
import com.macrosoft.modakserver.global.exception.CustomException;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
    public static final int MAX_EMOTE_LENGTH = 2;

    private final EntityManager entityManager;
    private final S3ImageComponent s3ImageComponent;
    private final LogService logService;
    private final MemberService memberService;
    private final CampfireService campfireService;
    private final LogRepository logRepository;
    private final LogImageRepository logImageRepository;
    private final EmotionRepository emotionRepository;

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

    @Override
    @Transactional
    public ImageDTO emotion(Member member, int campfirePin, Long imageId, String emote) {
        validateEmoteLength(emote);
        Member memberInDB = memberService.getMemberInDB(member);
        Campfire campfire = campfireService.findCampfireByPin(campfirePin);
        campfireService.validateMemberInCampfire(memberInDB, campfire);
        LogImage logImage = getLogImage(imageId);
        validateLogImageInCampfire(campfire, logImage);

        Optional<Emotion> existingEmotion = findOneEmotion(member, logImage);

        if (existingEmotion.isPresent()) {
            Emotion emotion = existingEmotion.get();
            emotion.updateEmotion(emote);
            emotionRepository.save(emotion);
        } else {
            Emotion newEmotion = Emotion.of(emote, logImage, memberInDB);
            emotionRepository.save(newEmotion);
            logImage.addEmote(newEmotion);
        }

        return ImageDTO.of(logImage);
    }

    @Override
    @Transactional
    public ImageDTO deleteEmotion(Member member, int campfirePin, Long imageId) {
        Member memberInDB = memberService.getMemberInDB(member);
        Campfire campfire = campfireService.findCampfireByPin(campfirePin);
        campfireService.validateMemberInCampfire(memberInDB, campfire);
        LogImage logImage = getLogImage(imageId);
        validateLogImageInCampfire(campfire, logImage);
        Set<Emotion> emotion = findEmotion(member, logImage);
        for (Emotion e : emotion) {
            validateEmotionUploader(e, memberInDB);
        }
        emotionRepository.deleteAll(emotion);
        entityManager.flush();
        logImage = getLogImage(imageId);
        return ImageDTO.of(logImage);
    }

    private Set<Emotion> findEmotion(Member member, LogImage logImage) {
        Set<Emotion> emotions = emotionRepository.findAllByMemberAndLogImage(member, logImage);
        if (emotions.isEmpty()) {
            throw new CustomException(EMOTION_NOT_FOUND);
        }
        return emotions;
    }

    private Optional<Emotion> findOneEmotion(Member member, LogImage logImage) {
        return emotionRepository.findByMemberAndLogImage(member, logImage);
    }

    private void validateEmotionUploader(Emotion emotion, Member member) {
        if (!emotion.getMember().equals(member)) {
            throw new CustomException(EMOTION_NOT_UPLOAD_USER);
        }
    }

    private void validateEmoteLength(String emote) {
        if (emote == null || emote.isBlank()) {
            throw new CustomException(EMOTE_EMPTY);
        }
        if (emote.length() > MAX_EMOTE_LENGTH) {
            throw new CustomException(EMOTE_TOO_LONG);
        }
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
