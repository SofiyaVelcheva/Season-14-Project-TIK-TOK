package com.tiktok.controller;

import com.tiktok.model.dto.userDTO.LoginRequestDTO;
import com.tiktok.model.dto.userDTO.UserLoginResponseDTO;
import com.tiktok.model.dto.userDTO.UserRegisterDTO;
import com.tiktok.model.entities.User;
import com.tiktok.model.exceptions.BadRequestException;
import com.tiktok.model.exceptions.NotFoundException;
import com.tiktok.model.exceptions.UnauthorizedException;
import com.tiktok.model.repository.UserRepository;
import com.tiktok.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@RestController
public class UserController extends GlobalController {
    @Autowired
    private UserService userService;

    @PostMapping("/auth")
    public ResponseEntity<UserLoginResponseDTO> login
            (@RequestBody LoginRequestDTO user,
             HttpSession session) {

        UserLoginResponseDTO result = userService.login(user);
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
    public ResponseEntity<UserRegisterDTO> register(@RequestBody User u) {
        if (!isPasswordValid(u)) {
            throw new BadRequestException("The password is not valid!");
        }

        // todo phone number

        if (!isValidName(u.getFirstName())) {
            throw new BadRequestException("The first name is not valid!");
        }

        if (!isValidName(u.getLastName())) {
            throw new BadRequestException("The last name is not valid!");
        }

        userRepository.save(u);

        UserRegisterDTO dto = modelMapper.map(u, UserRegisterDTO.class);

        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }


    //todo methods for service package
    private boolean isValidName(String name) {
        String validName = "^[A-Z][-a-zA-Z]+$";
        Pattern p = Pattern.compile(validName);
        Matcher m = p.matcher(name);
        System.out.println(m.find());
        return !m.find();
    }

    private boolean isPasswordValid(User u) {
        String validEmail = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$";

        // at least one digit [0-9]
        // at least one lowercase Latin character [a-z]
        // at least one uppercase Latin character [A-Z]
        // at least one special character
        // length: 8-20 symbols

        Pattern p = Pattern.compile(validEmail);
        Matcher m = p.matcher(u.getPassword());
        return m.find();
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

}
