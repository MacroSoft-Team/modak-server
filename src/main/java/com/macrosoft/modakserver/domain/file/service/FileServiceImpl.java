package com.macrosoft.modakserver.domain.file.service;

import static com.macrosoft.modakserver.domain.file.exception.FileErrorCode.INVALID_FILE_EXTENSION;

import com.macrosoft.modakserver.domain.file.component.S3ImageComponent;
import com.macrosoft.modakserver.global.exception.CustomException;
import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    public static final String IMAGE_FOLDER_NAME = "prod";
    private final S3Presigner presigner;
    private final S3ImageComponent s3ImageComponent;

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName;

    @Override
    public Map<String, String> getPresignedPutUrl(String extension) {
        if (isInvalidExtension(extension)) {
            throw new CustomException(INVALID_FILE_EXTENSION);
        }
        String fileName = createPath(extension);

        String presignUrl = createPresignedPutUrl(bucketName, fileName);
        return Map.of("url", presignUrl);
    }

    private String createPresignedPutUrl(String bucketName, String keyName) {
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(5)) // URL 은 5분간 유효하다.
                .putObjectRequest(objectRequest)
                .build();

        PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);
        log.info("Presigned URL: [{}]", presignedRequest.url().toString());
        log.info("HTTP method: [{}]", presignedRequest.httpRequest().method());

        return presignedRequest.url().toExternalForm();
    }


    @Override
    public void deleteImageFromS3(String imageName) {
        s3ImageComponent.deleteImageFromS3ByImageName(imageName);
    }

    private String createPath(String extension) {
        String fileId = createFileId();
        return String.format("%s/%s.%s", FileServiceImpl.IMAGE_FOLDER_NAME, fileId, extension);
    }

    private String createFileId() {
        return UUID.randomUUID().toString();
    }

    private boolean isInvalidExtension(String extension) {
        final Set<String> VALID_EXTENSIONS = Set.of(
                "jpg", "jpeg", "png", "webp", "gif", "bmp", "heif", // 이미지
                "mp4", "mov", "avi", "mkv", "webm", "flv"  // 비디오
        );
        return !VALID_EXTENSIONS.contains(extension.toLowerCase());
    }
}
