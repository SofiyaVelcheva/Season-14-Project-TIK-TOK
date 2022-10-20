package com.tiktok.model.dto.videoDTO;

import com.tiktok.model.entities.User;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class VideoWithoutOwnerDTO {

    private boolean isLive;
    private boolean isPrivate;
    private String description;

}
