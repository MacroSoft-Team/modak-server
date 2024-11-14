package com.macrosoft.modakserver.domain.file.component;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;
import com.macrosoft.modakserver.domain.file.exception.FireErrorCode;
import com.macrosoft.modakserver.global.exception.CustomException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@Component
public class S3ImageComponent {
    private final AmazonS3 s3Client;

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName;

    public String upload(MultipartFile image, String folderName) {
        if (image.isEmpty() || Objects.isNull(image.getOriginalFilename())) {
            throw new CustomException(FireErrorCode.EMPTY_FILE_EXCEPTION);
        }
        return this.uploadImage(image, folderName);
    }

    private String uploadImage(MultipartFile image, String folderName) {
        this.validateImageFileExtention(Objects.requireNonNull(image.getOriginalFilename()));
        try {
            return this.uploadImageToS3(image, folderName);
        } catch (IOException e) {
            throw new CustomException(FireErrorCode.IO_EXCEPTION_ON_IMAGE_UPLOAD);
        }
    }

    private void validateImageFileExtention(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            throw new CustomException(FireErrorCode.NO_FILE_EXTENTION);
        }

        String extention = filename.substring(lastDotIndex + 1).toLowerCase();
        List<String> allowedExtentionList = Arrays.asList("jpg", "jpeg", "png", "gif", "webp", "heif");

        if (!allowedExtentionList.contains(extention)) {
            throw new CustomException(FireErrorCode.INVALID_FILE_EXTENTION);
        }
    }

    private String uploadImageToS3(MultipartFile image, String folderName) throws IOException {
        String originalFilename = image.getOriginalFilename(); //원본 파일 명
        String extention = Objects.requireNonNull(originalFilename)
                .substring(originalFilename.lastIndexOf(".")); //확장자 명

//        String s3FileName = UUID.randomUUID().toString().substring(0, 10) + originalFilename; // 변경된 파일 명
        String s3FileName = folderName + UUID.randomUUID() + extention; // 매개변수로 받은 이름과 확장자로 파일명 생성

        InputStream is = image.getInputStream();
        byte[] bytes = IOUtils.toByteArray(is);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("image/" + extention);
        metadata.setContentLength(bytes.length);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

        try {
            PutObjectRequest putObjectRequest =
                    new PutObjectRequest(bucketName, s3FileName, byteArrayInputStream, metadata);
            s3Client.putObject(putObjectRequest); // put image to S3
        } catch (Exception e) {
            log.error("Failed to upload image to S3.", e);
            log.error("Bucket: {}, S3 Key: {}, Metadata: ContentType={}, ContentLength={}",
                    bucketName, s3FileName, metadata.getContentType(), metadata.getContentLength());
            throw new CustomException(FireErrorCode.PUT_OBJECT_EXCEPTION);
        } finally {
            byteArrayInputStream.close();
            is.close();
        }

        return s3Client.getUrl(bucketName, s3FileName).toString();
    }

    public void deleteImageFromS3ByImageAddress(String imageAddress) {
        String key = getKeyFromImageAddress(imageAddress);
        try {
            s3Client.deleteObject(new DeleteObjectRequest(bucketName, key));
        } catch (Exception e) {
            throw new CustomException(FireErrorCode.IO_EXCEPTION_ON_IMAGE_DELETE);
        }
    }

    public void deleteImageFromS3ByImageName(String imageName) {
        try {
            s3Client.deleteObject(new DeleteObjectRequest(bucketName, imageName));
        } catch (Exception e) {
            throw new CustomException(FireErrorCode.IO_EXCEPTION_ON_IMAGE_DELETE);
        }
    }

    private String getKeyFromImageAddress(String imageAddress) {
        try {
            URL url = new URL(imageAddress);
            String decodingKey = URLDecoder.decode(url.getPath(), "UTF-8");
            return decodingKey.substring(1); // 맨 앞의 '/' 제거
        } catch (MalformedURLException e) {
            throw new CustomException(FireErrorCode.MALFORMED_URL_EXCEPTION);
        } catch (UnsupportedEncodingException e) {
            throw new CustomException(FireErrorCode.UNSUPPORTED_ENCODING_EXCEPTION);
        }
    }
}