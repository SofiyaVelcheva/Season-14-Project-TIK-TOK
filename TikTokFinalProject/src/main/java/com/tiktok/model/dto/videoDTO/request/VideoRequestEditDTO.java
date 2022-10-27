package com.tiktok.model.dto.videoDTO.request;

import lombok.Data;

@Data
public class VideoRequestEditDTO {

    private boolean isPrivate;
    private String description;

}
