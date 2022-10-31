package com.tiktok.model.dto.video.response;


import com.tiktok.model.dto.user.UserResponseDTO;
import lombok.Data;

@Data
public class VideoResponseToCommentDTO {

    private int id;
    private UserResponseDTO owner;
}
