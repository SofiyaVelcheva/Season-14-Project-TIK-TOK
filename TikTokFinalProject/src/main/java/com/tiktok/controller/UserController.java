package com.tiktok.controller;

import com.tiktok.model.dto.TextResponseDTO;
import com.tiktok.model.dto.user.*;
import com.tiktok.model.dto.user.LoginRequestUserDTO;
import com.tiktok.model.dto.user.RegisterRequestUserDTO;
import com.tiktok.model.exceptions.BadRequestException;
import com.tiktok.model.exceptions.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
public class UserController extends GlobalController {
    @PostMapping("/auth")
    public ResponseEntity<UserResponseDTO> login(@Valid @RequestBody LoginRequestUserDTO user,
                                                 HttpServletRequest req) {
        if (isLogged(req)) {
            throw new BadRequestException("You are already logged in.");
        }
        UserResponseDTO result = userService.login(user);
        setSession(req, result.getId());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/users")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody RegisterRequestUserDTO u, HttpServletRequest req) {
        if (isLogged(req)) {
            throw new BadRequestException("You are already registered and log in.");
        }
        UserResponseDTO user = userService.register(u);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PutMapping("/users")
    public ResponseEntity<UserResponseDTO> edit(@Valid @RequestBody EditUserRequestDTO dto,
                                                HttpServletRequest req) {
        UserResponseDTO user = userService.edit(getUserIdFromSession(req), dto);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping("/users/pass")
    public ResponseEntity<UserResponseDTO> changePass(HttpServletRequest req,
                                                      @Valid @RequestBody ChangePassRequestUserDTO dto) {
        UserResponseDTO userDTO = userService.changePass(getUserIdFromSession(req), dto);
        req.getSession().invalidate();
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    @PutMapping("/users/photo")
    public ResponseEntity<UserResponseDTO> uploadProfilePhoto(HttpServletRequest req, @Valid @RequestParam MultipartFile file) {
        UserResponseDTO user = fileService.uploadProfilePhoto(getUserIdFromSession(req), file);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<TextResponseDTO> logout(HttpServletRequest req) {
        if (!isLogged(req)) {
            req.getSession().invalidate();
            throw new UnauthorizedException("You have to log in!");
        }
        req.getSession().invalidate();
        return new ResponseEntity<>(getResponseDTO("Log out!"), HttpStatus.OK);
    }


    @DeleteMapping("/users/{id}")
    public ResponseEntity<TextResponseDTO> deleteUser(@PathVariable int id,
                                                      HttpServletRequest req) {
        userService.deleteUser(id, getUserIdFromSession(req));
        req.getSession().invalidate();
        return new ResponseEntity<>(getResponseDTO("Your delete request was successful."), HttpStatus.OK);
    }

    @PostMapping("/users/{id}/sub")
    public ResponseEntity<TextResponseDTO> subscribe(@PathVariable(name = "id") int publisherId,
                                                     HttpServletRequest req) {
        userService.subscribe(publisherId, getUserIdFromSession(req));
        return new ResponseEntity<>(getResponseDTO("Your follow request was successful."), HttpStatus.OK);
    }

    @PostMapping("/users/{id}/unsub")
    public ResponseEntity<TextResponseDTO> unsubscribe(@PathVariable(name = "id") int publisherId,
                                                       HttpServletRequest req) {
        userService.unsubscribe(publisherId, getUserIdFromSession(req));
        return new ResponseEntity<>(getResponseDTO("Your unfollow request was successful."), HttpStatus.OK);
    }

    @GetMapping("/users/{uid}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable int uid) {
        UserResponseDTO dto = userService.getUser(uid);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping("/users/search")
    public ResponseEntity<List<UserResponseDTO>> searchUserByUsername(@RequestParam(value = "username", defaultValue = "") String username,
                                                                      @RequestParam(value = "page", defaultValue = "0") int page,
                                                                      @RequestParam(value = "perPage", defaultValue = "10") int perPage) {
        return new ResponseEntity<>(userService.getAllUsersByUsername(username, page, perPage), HttpStatus.OK);
    }

    @GetMapping("/users/sub")
    public ResponseEntity<List<PublisherUserDTO>> getAllMyPublishers(@RequestParam(value = "page", defaultValue = "0") int page,
                                                                     @RequestParam(value = "perPage", defaultValue = "10") int perPage,
                                                                     HttpServletRequest req) {
        return new ResponseEntity<>(userService.getAllMyPublishers(getUserIdFromSession(req), page, perPage), HttpStatus.OK);
    }

    @PostMapping("users/{userId}/verifyEmail")
    public ResponseEntity<com.tiktok.model.dto.TextResponseDTO> verifyEmail(@PathVariable(name = "userId") int userId,
                                                                            @RequestParam(value = "verificationCode") String verificationCode) {
        return new ResponseEntity<>(userService.verifyEmail(verificationCode, userId), HttpStatus.ACCEPTED);
    }
}
