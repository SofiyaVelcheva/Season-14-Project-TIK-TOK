package com.tiktok.controller;

import com.tiktok.model.entities.User;
import com.tiktok.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


@RestController
public class UserController extends GlobalController {
    @Autowired
    public UserRepository userRepository;
//    @PostMapping("/users")
//    public User register(@RequestBody User u) {
//
//
//        return u;
//    }

//    private boolean isPhoneValid(User u){
//        Pattern p = Pattern.compile("^([0|\\+[0-9]]{10})");
//        Matcher m = p.matcher(u.getPhoneNumber());
//        return m.find();
//    }


}
