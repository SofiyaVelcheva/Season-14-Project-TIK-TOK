package com.tiktok.model.dto.videoDTO;

import com.tiktok.model.dto.comments.CommentWithoutVideoDTO;
import com.tiktok.model.dto.userDTO.BasicUserResponseDTO;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class VideoWithoutOwnerDTO {

    private int id;
    private String videoUrl;
    private LocalDateTime uploadAt;
    private boolean isLive;
    private boolean isPrivate;
    private String description;
    private List<BasicUserResponseDTO> likers;
    private List<CommentWithoutVideoDTO> comments;
}
