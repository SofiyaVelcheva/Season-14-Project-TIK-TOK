package com.tiktok.model.dto.videoDTO;

import com.tiktok.model.dto.userDTO.PublisherUserDTO;
import com.tiktok.model.entities.Sound;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class VideoUploadResponseDTO {

    private int id; // todo remove
    private String videoUrl;
    private LocalDateTime uploadAt;
    private Sound sound;
    private String description;
    private int numberOfLikes;
    private int numberOfComments;
    private PublisherUserDTO publisher;

}
