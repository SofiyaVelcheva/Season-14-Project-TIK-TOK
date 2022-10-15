package com.tiktok.controller;

import com.tiktok.model.entities.User;
import com.tiktok.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController extends GlobalController {
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/users")
    public User register(@RequestBody User u) {
        userRepository.save(u);
        return u;
    }


}
