package com.tiktok.model.dto.hashtag;

import com.tiktok.model.dto.video.response.VideoResponseUploadDTO;
import lombok.Data;

import java.util.List;

@Data
public class HashtagResponseDTO {

    private int id;
    private String text;
    private List<VideoResponseUploadDTO> videos;

}
