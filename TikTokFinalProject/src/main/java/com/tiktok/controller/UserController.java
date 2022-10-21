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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
public class UserController extends GlobalController {
    @Autowired
    private UserService userService;

    @PostMapping("/auth")
    public ResponseEntity<LoginResponseUserDTO> login
            (@RequestBody LoginRequestUserDTO user,
             HttpServletRequest req) {
        HttpSession session = req.getSession();
        if (!session.isNew()) {
            session.invalidate();
            throw new BadRequestException("You are already logged");
        }
        LoginResponseUserDTO result = userService.login(user);
        if (result != null) {
            setSession(req, result.getId());
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else {
            throw new BadRequestException("Wrong Credentials");
        }
    }

    @PutMapping("/user")
    public ResponseEntity<EditUserResponseDTO> edit(
            @RequestBody EditUserRequestDTO dto,
            HttpServletRequest req) {
        EditUserResponseDTO user = userService.edit(getUserIdFromSession(req), dto);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("/users")
    public ResponseEntity<RegisterResponseUserDTO> register(
            @RequestBody RegisterRequestUserDTO u) {
        RegisterResponseUserDTO user = userService.register(u);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<String> delete(@PathVariable int id) {
        userService.deleteUserAccount(id);
        return new ResponseEntity<>("The user has been deleted successfully!",
                HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        session.invalidate();
        return new ResponseEntity<>("Log out!", HttpStatus.OK);
    }

    @PutMapping("/user/pass")
    public ResponseEntity<ChangePassResponseUserDTO> changePass(
            HttpServletRequest req,
            @RequestBody ChangePassRequestUserDTO dto) {
        ChangePassResponseUserDTO userDTO = userService.editPass(getUserIdFromSession(req), dto);
    return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }



//    @GetMapping("/bum")
//    public void proba(){
//        System.out.println(userRepository.findByUsername("Krasi").isPresent());
//    }
//    @PostMapping("/start")
//    // todo delete test method
//    public String start(HttpSession session) {
//        session.setAttribute("id", 12);
//        return "set attribute 12";
//    }
//
//    @PostMapping("/check")
//    // todo delete test method
//    public String check(HttpSession session) {
//
//        return "print check" + session.getAttribute(USER_ID);
//    }

}
