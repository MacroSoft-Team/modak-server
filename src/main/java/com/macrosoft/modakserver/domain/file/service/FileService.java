package com.macrosoft.modakserver.domain.file.service;

import java.util.Map;

public interface FileService {
    Map<String, String> getPresignedPutUrl(String extension);

    void deleteImageFromS3(String imageUrl);
}
