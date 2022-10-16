package com.tiktok.controller;

import com.tiktok.model.entities.Sound;
import com.tiktok.model.entities.User;
import com.tiktok.model.entities.Video;
import com.tiktok.model.repository.UserRepository;
import com.tiktok.model.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class VideoController extends GlobalController {
    @Autowired
    VideoRepository videoRepository;
    @Autowired
    UserRepository userRepository;

    @PostMapping("/videos")
    public Video uploadVideo (@RequestBody Video video){
        User user = userRepository.findById(1L).get();
        video.setOwner(user);
        video.setUploadAt(LocalDateTime.now());
        videoRepository.save(video);
        return video;
    }

}
