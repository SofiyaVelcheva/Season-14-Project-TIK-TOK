package com.tiktok.model.dto.messageDTO;

import com.tiktok.model.dto.userDTO.PublisherUserDTO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SendMessageResponseDTO {

    private int id;
    private String text;
    private LocalDateTime send_at;
    private PublisherUserDTO receiver;

}
