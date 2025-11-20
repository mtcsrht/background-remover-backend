package com.mtcsrht.bgremovebackend.api.service;

import com.mtcsrht.bgremovebackend.api.model.Dto.PictureDto;
import com.mtcsrht.bgremovebackend.api.model.Picture;

public class PictureMapper {

    public static PictureDto toDto(Picture picture) {
        PictureDto dto = new PictureDto();
        dto.setId(picture.getId());
        dto.setCreatedAt(picture.getCreatedAt());
        dto.setUrl("/files/" + picture.getObjectKey());
        return dto;
    }
}
