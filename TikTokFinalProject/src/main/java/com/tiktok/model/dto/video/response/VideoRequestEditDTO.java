package com.tiktok.model.dto.video.response;

import lombok.Data;

@Data
public class VideoRequestEditDTO {

    private boolean isPrivate;
    private String description;

}
