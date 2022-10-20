package com.tiktok.model.dto.videoDTO;

import com.tiktok.model.dto.userDTO.UserWithoutVideoDTO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VideoResponseDTO {
    private int id;
    private UserWithoutVideoDTO owner;
    private String videoUrl;
    private LocalDateTime uploadAt;
    private boolean isLive;
    private boolean isPrivate;
    private String description;

}
