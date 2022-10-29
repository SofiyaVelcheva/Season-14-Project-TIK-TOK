package com.tiktok.model.dto.videoDTO.response;

import lombok.Data;

@Data
public class VideoResponseMessageDTO {
    private String responseMessage;
    public VideoResponseMessageDTO(String responseMessage){
        this.responseMessage = responseMessage;
    }

}
