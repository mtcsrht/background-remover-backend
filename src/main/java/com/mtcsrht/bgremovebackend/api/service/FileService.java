package com.mtcsrht.bgremovebackend.api.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    /**
     * Store the given file under a folder (e.g. "user_1") and return an objectKey like "user_1/uuid.png".
     */
    String store(MultipartFile file, String folder);

    /**
     * Store raw bytes, useful when you already have the processed bytes from FastAPI.
     */
    String store(byte[] bytes, String folder, String filenameHint);

    /**
     * Load a file as a Spring Resource by its objectKey (e.g. "user_1/uuid.png").
     */
    Resource loadAsResource(String objectKey);

    /**
     * Delete a stored file (optional).
     */
    void delete(String objectKey);
}
