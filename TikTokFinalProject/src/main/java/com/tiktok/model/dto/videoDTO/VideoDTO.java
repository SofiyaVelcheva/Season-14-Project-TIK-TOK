package com.tiktok.model.dto.videoDTO;

import com.tiktok.model.dto.userDTO.UserWithoutVideoDTO;
import com.tiktok.model.entities.Sound;
import com.tiktok.model.entities.User;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class VideoDTO {

    private long id;
    private String videoUrl;
    private LocalDateTime uploadAt;
    private boolean isLive;
    private boolean isPrivate;
    private String description;
    private UserWithoutVideoDTO owner;


}
