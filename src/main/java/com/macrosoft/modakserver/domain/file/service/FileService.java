package com.macrosoft.modakserver.domain.file.service;

import static com.macrosoft.modakserver.domain.file.exception.FireErrorCode.INVALID_FILE_EXTENSION;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.macrosoft.modakserver.global.exception.CustomException;
import java.net.URL;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FileService {
    private final AmazonS3 s3Client;

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName;

    public Map<String, String> getPresignedUrl(String extension) {
        if (isInvalidExtension(extension)) {
            throw new CustomException(INVALID_FILE_EXTENSION);
        }
        String prefix = "dev";
        String fileName = createPath(prefix, extension);

        GeneratePresignedUrlRequest generatePresignedUrlRequest = getGeneratePresignedUrlRequest(bucketName, fileName);
        URL url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);

        return Map.of("url", url.toString());
    }

    private String createPath(String prefix, String extension) {
        String fileId = createFileId();
        return String.format("%s/%s.%s", prefix, fileId, extension);
    }

    private String createFileId() {
        return UUID.randomUUID().toString();
    }

    private GeneratePresignedUrlRequest getGeneratePresignedUrlRequest(String bucket, String fileName) {
        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucket, fileName)
                .withMethod(HttpMethod.PUT)
                .withExpiration(getPresignedUrlExpiration());

//        generatePresignedUrlRequest.addRequestParameter(
//                Headers.S3_CANNED_ACL,
//                CannedAccessControlList.PublicRead.toString()
//        );

        return generatePresignedUrlRequest;
    }

    private Date getPresignedUrlExpiration() {
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 5;
        expiration.setTime(expTimeMillis);

        return expiration;
    }

    private boolean isInvalidExtension(String extension) {
        final Set<String> VALID_EXTENSIONS = Set.of(
                "jpg", "jpeg", "png", "webp", "gif", "bmp", "heif", // 이미지
                "mp4", "mov", "avi", "mkv", "webm", "flv"  // 비디오
        );
        return !VALID_EXTENSIONS.contains(extension.toLowerCase());
    }
}
