package com.tiktok.service;

import com.tiktok.model.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class VideoService extends GlobalService{

    @Autowired
    private VideoRepository videoRepository;
}
