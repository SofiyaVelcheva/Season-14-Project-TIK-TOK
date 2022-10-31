package com.tiktok.model.dto.video.response;

import com.tiktok.model.dto.user.PublisherUserDTO;
import com.tiktok.model.entities.Sound;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class VideoResponseUploadDTO {

    private int id;
    private String videoUrl;
    private LocalDateTime uploadAt;
    private Sound sound;
    private String description;
    private int numberOfLikes;
    private int numberOfComments;
    private PublisherUserDTO publisher;

}
