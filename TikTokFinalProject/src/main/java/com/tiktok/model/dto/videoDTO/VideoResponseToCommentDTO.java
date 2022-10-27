package com.tiktok.model.dto.videoDTO;

import com.tiktok.model.dto.userDTO.LoginResponseUserDTO;
import lombok.Data;

@Data
public class VideoResponseToCommentDTO {

    private int id;
    private LoginResponseUserDTO owner;
}
