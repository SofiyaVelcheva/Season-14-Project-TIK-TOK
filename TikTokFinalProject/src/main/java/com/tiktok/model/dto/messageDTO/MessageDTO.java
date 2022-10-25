package com.tiktok.model.dto.messageDTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageDTO {

    private String text;
    private LocalDateTime send_at;

}
