package com.mtcsrht.bgremovebackend.api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class BgRemoveClient {
    private final RestTemplate restTemplate;
    private final String baseUrl;
    public BgRemoveClient(RestTemplate restTemplate, @Value("${bgremove.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public byte[] removeBackground(MultipartFile file) {
        try {
            byte[] bytes = file.getBytes();
            String fileName = file.getOriginalFilename();

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

            ByteArrayResource fileResource = new ByteArrayResource(bytes) {
                @Override
                public String getFilename() {
                    return fileName != null ? fileName : "image.png";
                }
            };

            body.add("file", fileResource);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, Object>> request =
                    new HttpEntity<>(body, headers);

            return restTemplate.postForObject(
                    baseUrl + "/remove-bg",
                    request,
                    byte[].class
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to read uploaded file for background removal", e);
        }
    }
}
