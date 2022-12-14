package com.tiktok.model.dto.video.response;

import com.tiktok.model.dto.comments.CommentWithoutVideoDTO;
import com.tiktok.model.dto.user.PublisherUserDTO;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class VideoResponseWithoutOwnerDTO {

    private int id;
    private String videoUrl;
    private LocalDateTime uploadAt;
    private boolean isLive;
    private boolean isPrivate;
    private String description;
    private List<PublisherUserDTO> likers;
    private List<CommentWithoutVideoDTO> comments;
}
