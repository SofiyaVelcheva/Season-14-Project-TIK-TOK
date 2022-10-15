package com.tiktok.controller;

import com.tiktok.model.entities.Sound;
import com.tiktok.model.entities.User;
import com.tiktok.model.entities.Video;
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

    @PostMapping("/videos")
    public Video uploadVideo (@RequestBody Video video){
//        video.setOwner(new User());
//        video.setUploadAt(LocalDateTime.now());
//        video.setVideoURL("111");
        videoRepository.save(video);
        return video;
    }

}
