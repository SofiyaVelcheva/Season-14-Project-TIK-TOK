package com.tiktok.model.dto.comments;

import com.tiktok.model.dto.userDTO.ChangePassResponseUserDTO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentWithoutVideoDTO {

    private ChangePassResponseUserDTO owner;
    private String text;
    private LocalDateTime uploadAt;
}
