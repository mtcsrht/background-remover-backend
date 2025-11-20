package com.mtcsrht.bgremovebackend.api.service;

import com.mtcsrht.bgremovebackend.api.model.Dto.PictureDto;
import com.mtcsrht.bgremovebackend.api.model.Picture;
import com.mtcsrht.bgremovebackend.api.model.User;
import com.mtcsrht.bgremovebackend.api.repository.PictureRepository;
import com.mtcsrht.bgremovebackend.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PictureService {

    private final PictureRepository pictureRepository;
    private final FileService fileService;
    private final String fileBaseUrl;
    private final UserRepository userRepository;
    private final BgRemoveClient bgRemoveClient;

    public PictureService(PictureRepository pictureRepository, FileService fileService, @Value("${app.file-base-url}") String fileBaseUrl, UserRepository userRepository, BgRemoveClient bgRemoveClient) {
        this.pictureRepository = pictureRepository;
        this.fileService = fileService;
        this.fileBaseUrl = fileBaseUrl;
        this.userRepository = userRepository;
        this.bgRemoveClient = bgRemoveClient;
    }

    public PictureDto uploadAndProcess(MultipartFile file, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()->new RuntimeException("User not found" + userId));

        byte[] processedBytes = bgRemoveClient.removeBackground(file);
        if (processedBytes == null) {
            throw new RuntimeException("Failed to remove background image");
        }

        String folder = "user_" + user.getId();
        String objectKey = fileService.store(processedBytes, folder, file.getOriginalFilename());

        Picture picture = new Picture(user, objectKey);
        pictureRepository.save(picture);

        PictureDto pictureDto = new PictureDto();
        pictureDto.setId(picture.getId());
        pictureDto.setCreatedAt(picture.getCreatedAt());
        pictureDto.setUrl(fileBaseUrl + "/" + objectKey);
        return pictureDto;
    }

    public List<PictureDto> listPicturesForUser(Long userId) {
        return pictureRepository.findAllByUserId(userId).stream()
                .map(picture -> {
                    PictureDto pictureDto = new PictureDto();
                    pictureDto.setId(picture.getId());
                    pictureDto.setCreatedAt(picture.getCreatedAt());
                    pictureDto.setUrl(fileBaseUrl + "/" + picture.getObjectKey());
                    return pictureDto;
                })
                .toList();
    }

    public PictureDto saveProcessedPicture(User user, byte[] processed, String filenameHint){
        String folderPath = fileBaseUrl + user.getId() + "/" + filenameHint;

        String objectKey = fileService.store(processed, folderPath, filenameHint);
        Picture picture = new Picture();
        pictureRepository.save(picture);

        PictureDto dto = new PictureDto();
        dto.setId(picture.getId());
        dto.setCreatedAt(picture.getCreatedAt());
        dto.setUrl(fileBaseUrl + "/" + objectKey);
        return dto;
    }
}