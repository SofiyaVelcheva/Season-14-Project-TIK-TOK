package com.tiktok.model.dto;

import com.tiktok.model.entities.Sound;
import com.tiktok.model.entities.User;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class VideoDTO {

    private User owner;
    private String videoUrl;
    private LocalDateTime uploadAt;
    private boolean isLive;
    private boolean isPrivate;
    private String description;

}
