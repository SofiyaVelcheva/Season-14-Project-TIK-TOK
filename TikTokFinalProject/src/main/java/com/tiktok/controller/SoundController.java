package com.tiktok.controller;

import com.tiktok.model.repository.SoundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SoundController extends GlobalController {

    @Autowired
    public SoundRepository soundRepository;




}
