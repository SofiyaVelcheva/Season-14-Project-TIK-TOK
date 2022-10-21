package com.tiktok.model.dto.comments;

import com.tiktok.model.entities.Comment;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AddResponseCommentDTO {

    private String text;
    private LocalDateTime uploadAt;
    private CommentWithoutUserDTO parentId;
}
