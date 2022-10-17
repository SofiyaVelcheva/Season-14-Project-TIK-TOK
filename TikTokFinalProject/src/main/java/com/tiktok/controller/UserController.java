package com.tiktok.controller;

import com.tiktok.model.dto.userDTO.UserRegisterDTO;
import com.tiktok.model.entities.User;
import com.tiktok.model.exceptions.BadRequestException;
import com.tiktok.model.exceptions.NotFoundException;
import com.tiktok.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@RestController
public class UserController extends GlobalController {

    @Autowired
    private UserRepository userRepository;

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
    public ResponseEntity<String> delete(@PathVariable long id) {

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


}
