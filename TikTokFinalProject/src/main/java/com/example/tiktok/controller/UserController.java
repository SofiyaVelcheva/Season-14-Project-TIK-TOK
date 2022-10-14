package com.example.tiktok.controller;

import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @GetMapping("/users")
    public String getAll() {
        return "blya blya";
    }
}
