package com.tiktok.model.dto.userDTO;

import lombok.Data;

@Data
public class UsernameResponseDTO {

    private String username;
    private String photoURL;
    private int numberOfSubscribers;
    private int numberOfVideos;

}
