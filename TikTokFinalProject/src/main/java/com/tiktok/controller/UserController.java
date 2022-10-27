package com.tiktok.controller;

import com.tiktok.model.dto.userDTO.*;
import com.tiktok.model.dto.userDTO.LoginRequestUserDTO;
import com.tiktok.model.dto.userDTO.LoginResponseUserDTO;
import com.tiktok.model.dto.userDTO.RegisterRequestUserDTO;
import com.tiktok.model.dto.userDTO.RegisterResponseUserDTO;
import com.tiktok.model.exceptions.BadRequestException;
import com.tiktok.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@RestController
public class UserController extends GlobalController {


    @PostMapping("/auth")
    public ResponseEntity<LoginResponseUserDTO> login
            (@Valid @RequestBody LoginRequestUserDTO user,
             HttpServletRequest req) {
        HttpSession session = req.getSession();
        LoginResponseUserDTO result = userService.login(user);
        if (result != null) {
            setSession(req, result.getId());
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else {
            session.invalidate();
            throw new BadRequestException("You are already logged.");
        }
    }

    @PostMapping("/users")
    public ResponseEntity<RegisterResponseUserDTO> register(
            @Valid @RequestBody RegisterRequestUserDTO u) {
        RegisterResponseUserDTO user = userService.register(u);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PutMapping("/users")
    public ResponseEntity<EditUserResponseDTO> edit(
            @Valid @RequestBody EditUserRequestDTO dto,
            HttpServletRequest req) {
        EditUserResponseDTO user = userService.edit(getUserIdFromSession(req), dto);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping("/users/pass")
    public ResponseEntity<ChangePassResponseUserDTO> changePass(
            HttpServletRequest req,
            @Valid @RequestBody ChangePassRequestUserDTO dto) {
        ChangePassResponseUserDTO userDTO = userService.changePass(getUserIdFromSession(req), dto);
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    @PutMapping("/users/photo")
    public ResponseEntity<EditProfilePhotoResponseDTO> uploadProfilePhoto(
            HttpServletRequest req,
            @Valid @RequestParam MultipartFile file) {
        EditProfilePhotoResponseDTO user = userService.
                uploadProfilePhoto(getUserIdFromSession(req), file);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        session.invalidate();
        return new ResponseEntity<>("Log out!", HttpStatus.OK);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable int id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/users/{id}/sub")
    public ResponseEntity<String> subscribe(@PathVariable(name = "id") int publisherId, HttpServletRequest req) {
        userService.subscribe(publisherId, getUserIdFromSession(req));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/users/{id}/unsubscribers")
    public ResponseEntity<String> unsubscribe(@PathVariable(name = "id") int publisherId, HttpServletRequest req) {
        userService.unsubscribe(publisherId, getUserIdFromSession(req));
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
