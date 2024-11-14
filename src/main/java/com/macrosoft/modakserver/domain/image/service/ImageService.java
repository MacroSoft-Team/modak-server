package com.macrosoft.modakserver.domain.image.service;

public interface ImageService {
    void deleteImageFromS3(String imageUrl);
}