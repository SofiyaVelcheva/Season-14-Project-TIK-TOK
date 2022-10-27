package com.tiktok.model.dto.comments;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AddResponseCommentDTO {

    private int id;
    private String text;
    private LocalDateTime uploadAt;
    private CommentWithoutUserDTO parentId;
}
