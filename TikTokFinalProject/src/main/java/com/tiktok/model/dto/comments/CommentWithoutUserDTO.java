package com.tiktok.model.dto.comments;

import com.tiktok.model.dto.userDTO.LoginResponseUserDTO;
import com.tiktok.model.dto.userDTO.WithoutPassResponseUserDTO;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class CommentWithoutUserDTO {

    private int id;
    private String text;
    private LocalDateTime uploadAt;
    private LoginResponseUserDTO owner;
}
