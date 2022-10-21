package com.tiktok.service;

import com.tiktok.model.entities.User;
import com.tiktok.model.entities.Video;
import com.tiktok.model.exceptions.NotFoundException;
import com.tiktok.model.repository.SoundRepository;
import com.tiktok.model.repository.UserRepository;
import com.tiktok.model.repository.VideoRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class GlobalService {

    @Autowired
    protected ModelMapper modelMapper;

    @Autowired
    protected VideoRepository videoRepository;
    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected SoundRepository soundRepository;


    protected Video getVideoById(int videoId){
        return videoRepository.findById(videoId).orElseThrow(() -> new NotFoundException("Video not found"));
    }
    protected User getUserById(int userId){
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
    }
}