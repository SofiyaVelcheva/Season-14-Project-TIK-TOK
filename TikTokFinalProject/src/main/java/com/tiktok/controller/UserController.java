package com.tiktok.controller;

import com.tiktok.model.dto.userDTO.*;
import com.tiktok.model.dto.userDTO.LoginRequestUserDTO;
import com.tiktok.model.dto.userDTO.LoginResponseUserDTO;
import com.tiktok.model.dto.userDTO.RegisterRequestUserDTO;
import com.tiktok.model.dto.videoDTO.VideoUploadResponseDTO;
import com.tiktok.model.exceptions.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

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
    public ResponseEntity<BasicUserResponseDTO> register(
            @Valid @RequestBody RegisterRequestUserDTO u) {
        BasicUserResponseDTO user = userService.register(u);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PutMapping("/users")
    public ResponseEntity<BasicUserResponseDTO> edit(
            @Valid @RequestBody EditUserRequestDTO dto,
            HttpServletRequest req) {
        BasicUserResponseDTO user = userService.edit(getUserIdFromSession(req), dto);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping("/users/pass")
    public ResponseEntity<BasicUserResponseDTO> changePass(
            HttpServletRequest req,
            @Valid @RequestBody ChangePassRequestUserDTO dto) {
        BasicUserResponseDTO userDTO = userService.changePass(getUserIdFromSession(req), dto);
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    @PutMapping("/users/photo")
    public ResponseEntity<BasicUserResponseDTO> uploadProfilePhoto(
            HttpServletRequest req,
            @Valid @RequestParam MultipartFile file) {
        BasicUserResponseDTO user = userService.
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

    @PostMapping("/users/{id}/unsub")
    public ResponseEntity<String> unsubscribe(@PathVariable(name = "id") int publisherId, HttpServletRequest req) {
        userService.unsubscribe(publisherId, getUserIdFromSession(req));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("users/{uid}")
    public ResponseEntity<WithoutPassResponseUserDTO> getUser(@PathVariable int uid){
        WithoutPassResponseUserDTO dto = userService.getUser(uid);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<UsernameResponseDTO>> searchUserByUsername(@RequestParam(value = "username", defaultValue = "") String username,
                                                                          @RequestParam(value = "page", defaultValue = "0")int page,
                                                                          @RequestParam(value = "perPage", defaultValue = "10") int perPage ){
        return new ResponseEntity<>(userService.getAllUsersByUsername(username, page, perPage), HttpStatus.OK);
    }
    @GetMapping("/videos/sub")
    public ResponseEntity<List<VideoUploadResponseDTO>> getVideosPublishers(@RequestParam(value = "page", defaultValue = "0")int page,
                                                                             @RequestParam(value = "perPage", defaultValue = "10") int perPage,
                                                                             HttpServletRequest req ){
        return new ResponseEntity<>(userService.getVideosPublishers(getUserIdFromSession(req), page, perPage), HttpStatus.OK);
    }

    @GetMapping("/sub")
    public ResponseEntity<List<PublisherUserDTO>> getAllMyPublishers(@RequestParam(value = "page", defaultValue = "0") int page,
                                                                   @RequestParam(value = "perPage", defaultValue = "10") int perPage,
                                                                   HttpServletRequest req){
        return new ResponseEntity<>(userService.getAllMyPublishers(getUserIdFromSession(req), page, perPage), HttpStatus.OK);
    }






}
