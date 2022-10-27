package com.tiktok.model.dto.videoDTO.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VideoResponseDTO {

    private int id;
    private String videoUrl;
    private LocalDateTime uploadAt;
    private boolean isLive;
    private boolean isPrivate;
    private String description;
}
