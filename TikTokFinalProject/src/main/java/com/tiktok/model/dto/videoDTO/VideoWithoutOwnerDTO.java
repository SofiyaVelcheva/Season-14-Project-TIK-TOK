package com.tiktok.model.dto.videoDTO;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class VideoWithoutOwnerDTO {

    private int id;
    private String videoUrl;
    private LocalDateTime uploadAt;
    private boolean isLive;
    private boolean isPrivate;
    private String description;


}
