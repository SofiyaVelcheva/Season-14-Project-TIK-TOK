package com.tiktok.model.dto.comments;

import lombok.Data;

@Data
public class CommentResponseMessageDTO {
    private String responseMessage;

    public CommentResponseMessageDTO(String responseMessage){
        this.responseMessage = responseMessage;
    }

}
