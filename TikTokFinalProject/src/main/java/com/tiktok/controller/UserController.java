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
                                                 HttpServletRequest request) {
        if (isLogged(request)) {
            throw new BadRequestException("You are already logged in.");
        }
        UserResponseDTO result = userService.login(user);
        setSession(request, result.getId());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/users")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody RegisterRequestUserDTO dto,
                                                    HttpServletRequest request) {
        if (isLogged(request)) {
            throw new BadRequestException("You are already registered and log in.");
        }
        UserResponseDTO user = userService.register(dto);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PutMapping("/users")
    public ResponseEntity<UserResponseDTO> edit(@Valid @RequestBody EditUserRequestDTO dto,
                                                HttpServletRequest request) {
        UserResponseDTO user = userService.edit(getUserIdFromSession(request), dto);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping("/users/pass")
    public ResponseEntity<UserResponseDTO> changePass(@Valid @RequestBody ChangePassRequestUserDTO dto,
                                                      HttpServletRequest request) {
        UserResponseDTO userDTO = userService.changePass(getUserIdFromSession(request), dto);
        request.getSession().invalidate();
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    @PutMapping("/users/photo")
    public ResponseEntity<UserResponseDTO> uploadProfilePhoto(@Valid @RequestParam MultipartFile file,
                                                              HttpServletRequest request) {
        return new ResponseEntity<>(fileService.uploadProfilePhoto(getUserIdFromSession(request), file), HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<TextResponseDTO> logout(HttpServletRequest request) {
        if (!isLogged(request)) {
            request.getSession().invalidate();
            throw new UnauthorizedException("You have to log in!");
        }
        request.getSession().invalidate();
        return new ResponseEntity<>(getResponseDTO("Log out!"), HttpStatus.OK);
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<TextResponseDTO> deleteUser(@PathVariable int userId,
                                                      HttpServletRequest request) {
        userService.deleteUser(userId, getUserIdFromSession(request));
        request.getSession().invalidate();
        return new ResponseEntity<>(getResponseDTO("Your delete request was successful."), HttpStatus.OK);
    }

    @PostMapping("/users/{userId}/sub")
    public ResponseEntity<TextResponseDTO> subscribe(@PathVariable(name = "userId") int publisherId,
                                                     HttpServletRequest req) {
        userService.subscribe(publisherId, getUserIdFromSession(req));
        return new ResponseEntity<>(getResponseDTO("Your follow request was successful."), HttpStatus.OK);
    }

    @PostMapping("/users/{userId}/unsub")
    public ResponseEntity<TextResponseDTO> unsubscribe(@PathVariable(name = "userId") int publisherId,
                                                       HttpServletRequest request) {
        userService.unsubscribe(publisherId, getUserIdFromSession(request));
        return new ResponseEntity<>(getResponseDTO("Your unfollow request was successful."), HttpStatus.OK);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable int userId) {
        return new ResponseEntity<>(userService.getUser(userId), HttpStatus.OK);
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

    @GetMapping("users/verifyEmail/{token}")
    public ResponseEntity<TextResponseDTO> verifyRegistration(@PathVariable String token) {
        return new ResponseEntity<>(userService.verifyEmail(token), HttpStatus.OK);
    }


}
