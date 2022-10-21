package com.tiktok.service;

import com.tiktok.model.dto.userDTO.*;
import com.tiktok.model.entities.User;
import com.tiktok.model.exceptions.BadRequestException;
import com.tiktok.model.exceptions.NotFoundException;
import com.tiktok.model.exceptions.UnauthorizedException;
import com.tiktok.model.repository.UserRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserService {
//
    //
    //
    //
    //

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    public LoginResponseUserDTO login(LoginRequestUserDTO user) {

        // hash password
        user.setPassword(DigestUtils.sha256Hex(user.getPassword()));

        Optional<User> u = userRepository.
                findByUsernameAndPassword(user.getUsername(), user.getPassword());

        if (!u.isPresent()) {
            throw new BadRequestException("Username or password invalid!");
        }

        u.get().setLastLogin(LocalDateTime.now());
        userRepository.save(u.get());

        return modelMapper.map(u.get(), LoginResponseUserDTO.class);

    }

    public RegisterResponseUserDTO register(RegisterRequestUserDTO u) {

        validationUsername(u.getUsername());
        validationPassword(u.getPassword(), u.getConfirmPassword());
        validationName(u.getFirstName());
        validationName(u.getLastName());
        validationEmail(u.getEmail());
        validationPhone(u.getPhoneNumber());
        validationBirthday(u.getDateOfBirth());

        // encode password
        u.setPassword(DigestUtils.sha256Hex(u.getPassword()));

        User user = modelMapper.map(u, User.class);

        userRepository.save(user);

        return modelMapper.map(u, RegisterResponseUserDTO.class);
    }

    private void checkForEmptyField(String field) {
        if (field.isBlank()) {
            throw new BadRequestException("Empty row");
        }
    }

    private void validationUsername(String username) {
        // What characters can I use in my TikTok username?
        // Your username can only contain letters, numbers, underscores, and periods.
        // There is also a 24-character limit.
        // nonbinary string comparisons are case-insensitive by default
        // nonbinary strings (CHAR, VARCHAR, TEXT)

        checkForEmptyField(username);

        if (username.length() > 24) {
            throw new BadRequestException("Username length should be maximum 24 symbols");
        }

        String validUserName = "^[A-Za-z\\d._]{1,24}$";

        Pattern p = Pattern.compile(validUserName);
        Matcher m = p.matcher(username);

        if (!m.find()) {
            throw new BadRequestException("Invalid username, should be start with capital letter" +
                    "should be contain letters, numbers, underscores, and periods");
        }

        if (userRepository.findByUsername(username).isPresent()) {
            throw new BadRequestException("User with this username already exist.");
        }

    }

    private void validationName(String name) {
        checkForEmptyField(name);

        if (name.length() > 50) {
            throw new BadRequestException("Name length should be maximum 50 symbols");
        }

        String validName = "^[A-Z][-a-zA-Z]+$";

        Pattern p = Pattern.compile(validName);
        Matcher m = p.matcher(name);

        if (!m.find()) {
            throw new BadRequestException("Invalid name, should be start with capital letter");
        }
    }

    private void validationPassword(String password, String confirmPassword) {

        checkForEmptyField(password);
        checkForEmptyField(confirmPassword);

        if (password.length() < 8 || password.length() > 20) {
            throw new BadRequestException("The size length should be between 8 and 20 symbols");
        }

        if (!password.equals(confirmPassword)) {
            throw new BadRequestException("Password and confirm password is not equals.");
        }

        String validPassword = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$";

        // at least one digit [0-9]
        // at least one lowercase Latin character [a-z]
        // at least one uppercase Latin character [A-Z]
        // at least one special character
        // length: 8-20 symbols

        Pattern p = Pattern.compile(validPassword);
        Matcher m = p.matcher(password);

        if (!m.find()) {
            throw new BadRequestException("Password should contain at least one special symbol, at least one digit, " +
                    "at least one capital && at least one small letter.");
        }
    }

    private void validationPhone(String phoneNumber) {

        checkForEmptyField(phoneNumber);

        //String validPhone = "^([+]3598)|08||\\+8[7-9][0-9]{7}$";
        String validPhone = "^(\\+\\d{1,2}\\s?)?1?\\-?\\.?\\s?\\(?\\d{3}\\)?[\\s.-]?\\d{3}[\\s.-]?\\d{4}$";
        Pattern p = Pattern.compile(validPhone);
        Matcher m = p.matcher(phoneNumber);

        if (!m.find()) {
            throw new BadRequestException("Invalid phone number");
        }

        if (userRepository.findByPhoneNumber(phoneNumber).isPresent()) {
            throw new BadRequestException("User with this phone number already exist.");
        }
    }

    private void validationEmail(String email) {

        checkForEmptyField(email);

        if (email.length() > 50) {
            throw new BadRequestException("Email length should be maximum 50 symbols");
        }

        String validEmail = "^[a-zA-Z\\d+_.-]+@[a-zA-Z\\d-]+.[a-zA-Z]+$";

        Pattern p = Pattern.compile(validEmail);
        Matcher m = p.matcher(email);

        if (!m.find()) {
            throw new BadRequestException("Invalid email");
        }

        if (userRepository.findByEmail(email).isPresent()) {
            throw new BadRequestException("User with this email already exist.");
        }

    }

    private void validationBirthday(LocalDate dateOfBirth) {

        checkForEmptyField(dateOfBirth.toString());

        Period p = Period.between(dateOfBirth, LocalDate.now());
        if (p.getYears() < 13 || p.getYears() > 100) {
            throw new UnauthorizedException("You should be more 13 years old.");
        }
    }

    public void deleteUserAccount(int userId) {

        User u = getUserById(userId);

        u.setUsername("Delete on " + LocalDateTime.now());
        u.setPassword("Delete on " + LocalDateTime.now());
        u.setFirstName("Delete on " + LocalDateTime.now());
        u.setLastName("Delete on " + LocalDateTime.now());
        u.setEmail("Delete on " + LocalDateTime.now());
        u.setPhoneNumber("Delete");
        //u.setDateOfBirth(null);
        //u.setProfilePhoto("DELETED ON " + LocalDateTime.now());
        userRepository.save(u);

    }

     private User getUserById(int id){
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    public EditUserResponseDTO edit(int id, EditUserRequestDTO dto) {
        User u = getUserById(id);

        if (dto.getFirstName() != null) {
            validationName(dto.getFirstName());
            u.setFirstName(dto.getFirstName());
        }

        if (dto.getLastName() != null) {
            validationName(dto.getLastName());
            u.setLastName(dto.getLastName());
        }

        if (dto.getEmail() != null) {
            validationEmail(dto.getEmail());
            u.setEmail(dto.getEmail());
        }

        if (dto.getPhoneNumber() != null) {
            validationPhone(dto.getPhoneNumber());
            u.setPhoneNumber(dto.getPhoneNumber());
        }

        userRepository.save(u);

        return modelMapper.map(u, EditUserResponseDTO.class);
    }
}
