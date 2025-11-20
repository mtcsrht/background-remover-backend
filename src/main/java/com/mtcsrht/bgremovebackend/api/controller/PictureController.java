package com.mtcsrht.bgremovebackend.api.controller;


import com.mtcsrht.bgremovebackend.api.model.Dto.PictureDto;
import com.mtcsrht.bgremovebackend.api.model.User;
import com.mtcsrht.bgremovebackend.api.service.PictureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/pictures")
public class PictureController {
    private static final Logger log = LoggerFactory.getLogger(PictureController.class);
    private final PictureService pictureService;

    public PictureController(PictureService pictureService) {
        this.pictureService = pictureService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity saveProcessedPicture(@RequestParam("file") MultipartFile file, @AuthenticationPrincipal User user){
        if (user == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long userId = user.getId();

        PictureDto pictureDto = pictureService.uploadAndProcess(file, userId);
        return ResponseEntity.ok(pictureDto);
    }


    @GetMapping
    public ResponseEntity<List<PictureDto>> getAllPictures(Authentication authentication){
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Long userId = user.getId();
        List<PictureDto> pictureDtos = pictureService.listPicturesForUser(userId);
        return ResponseEntity.ok(pictureDtos);
    }
}
