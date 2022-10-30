package com.tiktok.model.dto.comments;

import com.tiktok.model.dto.user.UserResponseDTO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentWithoutVideoDTO {

    private int id;
    private UserResponseDTO owner;
    private String text;
    private LocalDateTime uploadAt;
}
