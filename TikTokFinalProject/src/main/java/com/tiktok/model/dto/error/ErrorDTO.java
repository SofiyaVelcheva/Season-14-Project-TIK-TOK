package com.tiktok.model.dto.error;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ErrorDTO {

    private int status;
    private LocalDateTime time;
    private String msg;

}
