package com.tiktok.model.dto.comments;

import lombok.*;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
public class CommentWithTextDTO {
    private String text;
}
