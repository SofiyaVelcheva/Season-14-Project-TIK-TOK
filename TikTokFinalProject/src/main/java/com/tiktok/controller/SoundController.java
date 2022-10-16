package com.tiktok.controller;

import com.tiktok.model.entities.Sound;
import com.tiktok.model.entities.Video;
import com.tiktok.model.repository.SoundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
@RestController
public class SoundController extends GlobalController {

    @Autowired
    public SoundRepository soundRepository;




}
