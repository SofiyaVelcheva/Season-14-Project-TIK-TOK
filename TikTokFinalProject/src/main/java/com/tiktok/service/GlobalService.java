package com.tiktok.service;

import com.tiktok.model.repository.SoundRepository;
import com.tiktok.model.repository.VideoRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class GlobalService {

    @Autowired
    protected ModelMapper modelMapper;

    @Autowired
    protected VideoRepository videoRepository;


    @Autowired
    SoundRepository soundRepository;

}
