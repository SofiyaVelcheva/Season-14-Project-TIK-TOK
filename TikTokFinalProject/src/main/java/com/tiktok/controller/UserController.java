package com.tiktok.controller;

import com.tiktok.model.entities.User;
import com.tiktok.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
public class UserController extends GlobalController {
    @Autowired
    public UserRepository userRepository;
    @PostMapping("/users")
    public User register(@RequestBody User u) {
        userRepository.save(u);
        return u;
    }


}
