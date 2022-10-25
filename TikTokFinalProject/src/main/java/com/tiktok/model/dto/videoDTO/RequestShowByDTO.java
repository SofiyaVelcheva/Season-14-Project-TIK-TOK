package com.tiktok.model.dto.videoDTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RequestShowByDTO {

    private String title;
    private String username;
    private String uploadAt;
    private String uploadTo;

}
