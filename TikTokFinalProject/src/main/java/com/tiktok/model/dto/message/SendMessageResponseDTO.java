package com.tiktok.model.dto.message;

import com.tiktok.model.dto.user.PublisherUserDTO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SendMessageResponseDTO {

    private int id;
    private String text;
    private LocalDateTime send_at;
    private PublisherUserDTO receiver;

}
