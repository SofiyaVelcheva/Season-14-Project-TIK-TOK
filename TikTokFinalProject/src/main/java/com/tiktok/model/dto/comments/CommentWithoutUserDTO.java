package com.tiktok.model.dto.comments;

import com.tiktok.model.dto.userDTO.UserResponseDTO;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class CommentWithoutUserDTO {

    private int id;
    private String text;
    private LocalDateTime uploadAt;
    private UserResponseDTO owner;
}
