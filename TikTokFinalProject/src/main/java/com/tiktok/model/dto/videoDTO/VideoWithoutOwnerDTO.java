package com.tiktok.model.dto.videoDTO;

import com.tiktok.model.dto.userDTO.EditUserResponseDTO;
import com.tiktok.model.entities.User;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class VideoWithoutOwnerDTO {

    private int id;
    private String videoUrl;
    private LocalDateTime uploadAt;
    private boolean isLive;
    private boolean isPrivate;
    private String description;
    private List<EditUserResponseDTO> likers;
}
