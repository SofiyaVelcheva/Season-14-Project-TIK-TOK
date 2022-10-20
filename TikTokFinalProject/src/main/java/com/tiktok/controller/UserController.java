package com.tiktok.controller;

import com.tiktok.model.dto.userDTO.LoginRequestUserDTO;
import com.tiktok.model.dto.userDTO.LoginResponseUserDTO;
import com.tiktok.model.dto.userDTO.RegisterRequestUserDTO;
import com.tiktok.model.dto.userDTO.RegisterResponseUserDTO;
import com.tiktok.model.entities.User;
import com.tiktok.model.exceptions.BadRequestException;
import com.tiktok.model.exceptions.NotFoundException;
import com.tiktok.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Optional;


@RestController
public class UserController extends GlobalController {
    @Autowired
    private UserService userService;

    @PostMapping("/auth")
    public ResponseEntity<LoginResponseUserDTO> login
            (@RequestBody LoginRequestUserDTO user,
             HttpSession session) {

        LoginResponseUserDTO result = userService.login(user);
        if (result != null) {
            session.setAttribute(LOGGED, true);
            session.setAttribute(USER_ID, result.getId());
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else {
            throw new BadRequestException("Wrong Credentials");
        }

    }

//    public void edit() {
//
//        //check login - method check login
//        //service give request body and  id from session
//    }


    @PostMapping("/users")
    public ResponseEntity<RegisterResponseUserDTO> register(@RequestBody RegisterRequestUserDTO u) {

        RegisterResponseUserDTO user = userService.register(u);


        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }




    @DeleteMapping(name = "/users/{id}")
    public ResponseEntity<String> delete(@PathVariable long id, HttpSession session) {

        Optional<User> optional = userRepository.findById(id);

        if (!optional.isPresent()) {
            System.out.println("is present");
            throw new NotFoundException("The user not found!");
        }

        User u = modelMapper.map(optional.get(), User.class);
        userRepository.deleteById(id);

        return new ResponseEntity<>("The user has been deleted successfully!",
                HttpStatus.OK);

    }

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

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        session.invalidate();
        return new ResponseEntity<>("Log out!", HttpStatus.OK);
    }

    @GetMapping("/bum")
    public void proba(){
        System.out.println(userRepository.findByUsername("Krasi").isPresent());
    }

}
