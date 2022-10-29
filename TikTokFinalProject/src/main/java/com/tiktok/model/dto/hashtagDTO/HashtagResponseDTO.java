package com.tiktok.model.dto.hashtagDTO;

import com.tiktok.model.dto.videoDTO.VideoUploadResponseDTO;
import lombok.Data;

import java.util.List;

@Data
public class HashtagResponseDTO {

    private int id;
    private String text;
    private List<VideoUploadResponseDTO> videos;

}
