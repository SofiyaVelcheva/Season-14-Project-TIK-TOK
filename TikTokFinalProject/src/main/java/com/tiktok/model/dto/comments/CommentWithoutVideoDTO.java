package com.tiktok.model.dto.comments;

import com.tiktok.model.dto.userDTO.BasicUserResponseDTO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentWithoutVideoDTO {

    private BasicUserResponseDTO owner;
    private String text;
    private LocalDateTime uploadAt;
}
