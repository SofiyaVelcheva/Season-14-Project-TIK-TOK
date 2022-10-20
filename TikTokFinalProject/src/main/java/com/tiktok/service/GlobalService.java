package com.tiktok.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class GlobalService {

    @Autowired
    public ModelMapper modelMapper;

}
