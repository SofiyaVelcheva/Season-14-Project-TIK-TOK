package com.tiktok.model.dto.userDTO;

import lombok.Data;

@Data
public class WithoutPassResponseUserDTO {

    private String username;
    private String firstName;
    private String lastName;
    private String photoURL;
    private int numberOfVideos;
    private int numberOfSubscribers;
    private int numberOfSubscribeTo;
    // todo numberOfLikes

}
