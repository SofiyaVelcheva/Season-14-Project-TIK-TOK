package com.tiktok.model.dto.videoDTO.response;


import com.tiktok.model.dto.userDTO.UserResponseDTO;
import lombok.Data;

@Data
public class VideoResponseToCommentDTO {

    private int id;
    private UserResponseDTO owner;
}
