package com.tiktok.model.dto.videoDTO;

import com.tiktok.model.dto.userDTO.LoginResponseUserDTO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostResponsVideoDTO {

    private int id;
    private String videoUrl;
    private LocalDateTime uploadAt;
    private boolean isLive;
    private boolean isPrivate;
    private String description;
}
