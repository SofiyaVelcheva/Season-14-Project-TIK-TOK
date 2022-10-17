package com.tiktok.controller;

import com.tiktok.model.dto.videoDTO.VideoDTO;
import com.tiktok.model.entities.Sound;
import com.tiktok.model.entities.User;
import com.tiktok.model.entities.Video;
import com.tiktok.model.exceptions.NotFoundException;
import com.tiktok.model.repository.SoundRepository;
import com.tiktok.model.repository.UserRepository;
import com.tiktok.model.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
public class VideoController extends GlobalController {

    @Autowired
    VideoRepository videoRepository;
    @Autowired
    UserRepository userRepository;

    @Autowired
    SoundRepository soundRepository;


    @PostMapping("/videos/{id}") // TODO added user id
    public VideoDTO uploadVideo(@RequestBody Video video, @PathVariable long id) {
        System.out.println(video.isPrivate());
        System.out.println(video.isLive());
        Optional<User> optUser = userRepository.findById(id);//TODO need to be get from the session probably
        if (optUser.isPresent()) {
            User user = optUser.get();
            video.setOwner(user);
            video.setUploadAt(LocalDateTime.now());
            videoRepository.save(video); // TODO need to be added URL to video
            if (!video.isPrivate()) {
                Sound sound = new Sound();
                sound.setOwner(video.getOwner());
                sound.setUploadAt(LocalDateTime.now());
                sound.setSoundUrl("222"); //TODO need to be changed
                sound.setTitle("Original sound - " + video.getOwner().getUsername());
                soundRepository.save(sound); //TODO need to be with service
            }
            VideoDTO dto = new VideoDTO();  // TODO is it necessary to be in videoDTO??
            dto.setDescription(video.getDescription());
            dto.setUploadAt(video.getUploadAt());
            dto.setLive(video.isLive());
            dto.setPrivate(video.isPrivate());
            dto.setOwner(video.getOwner());
            dto.setVideoUrl(video.getVideoUrl());
            return dto;
        } else {
            throw new NotFoundException("The user is invalid");
        }
    }
}