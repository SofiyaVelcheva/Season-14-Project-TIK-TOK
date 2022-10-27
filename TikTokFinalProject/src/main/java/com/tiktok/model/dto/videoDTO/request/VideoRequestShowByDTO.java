package com.tiktok.model.dto.videoDTO.request;

import lombok.Data;

@Data
public class VideoRequestShowByDTO {

    private String title;
    private String username;
    private String uploadAt;
    private String uploadTo;

}
