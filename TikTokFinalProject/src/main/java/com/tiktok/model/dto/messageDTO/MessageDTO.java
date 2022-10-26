package com.tiktok.model.dto.messageDTO;

import com.tiktok.model.dto.userDTO.PublisherUserDTO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageDTO {

    private PublisherUserDTO sender;
    private int id;
    private String text;
    private LocalDateTime send_at;

}
