package com.tiktok.model.dto.comments;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentWithoutVideoDTO {

    private String text;
    private LocalDateTime uploadAt;
}
