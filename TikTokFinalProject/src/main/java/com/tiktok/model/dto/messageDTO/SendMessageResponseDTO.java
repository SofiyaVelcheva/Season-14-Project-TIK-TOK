package com.tiktok.model.dto.messageDTO;

import com.tiktok.model.dto.userDTO.PublisherUserDTO;
import lombok.Data;

@Data
public class SendMessageResponseDTO {

    private int id;
    private String text;
    private PublisherUserDTO receiver;

}
