package com.tiktok.model.dto.message;

import com.tiktok.model.dto.user.PublisherUserDTO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageDTO {

    private PublisherUserDTO sender;
    private int id;
    private String text;
    private LocalDateTime send_at;

}
