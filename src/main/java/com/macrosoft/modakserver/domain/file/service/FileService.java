package com.macrosoft.modakserver.domain.file.service;

import java.util.Map;

public interface FileService {
    Map<String, String> getPresignedUrl(String extension);

    void deleteImageFromS3(String imageUrl);
}
