package com.tiktok.controller;

import com.tiktok.model.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    private UserRepository userRepository;
    @GetMapping("/users")
    public String getAll() {
        return "MIraaaa poginahme";
    }





}
