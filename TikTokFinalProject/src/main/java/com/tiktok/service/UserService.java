package com.tiktok.service;

import com.tiktok.model.dto.userDTO.*;
import com.tiktok.model.entities.User;
import com.tiktok.model.exceptions.BadRequestException;
import com.tiktok.model.exceptions.UnauthorizedException;
import com.tiktok.model.repository.UserRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.LocalDate;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserService extends GlobalService {


    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;

    public LoginResponseUserDTO login(LoginRequestUserDTO dto) {
        // hash password
        dto.setPassword(DigestUtils.sha256Hex(dto.getPassword()));
        Optional<User> u = userRepository.
                findByUsernameAndPassword(dto.getUsername(), dto.getPassword());
        if (u.isEmpty()) {
            throw new BadRequestException("Username or password invalid!");
        }
        u.get().setLastLogin(LocalDateTime.now());
        userRepository.save(u.get());
        return modelMapper.map(u.get(), LoginResponseUserDTO.class);
    }

    public RegisterResponseUserDTO register(RegisterRequestUserDTO dto) {
        validationUsername(dto.getUsername());
        validationPassword(dto.getPassword(), dto.getConfirmPassword());
        validationName(dto.getFirstName());
        validationName(dto.getLastName());
        validationEmail(dto.getEmail());
        validationPhone(dto.getPhoneNumber());
        validationBirthday(dto.getDateOfBirth());
        // encode password
        dto.setPassword(DigestUtils.sha256Hex(dto.getPassword()));
        User user = modelMapper.map(dto, User.class);
        user.setVerifiedEmail(false);
        userRepository.save(user);
        return modelMapper.map(dto, RegisterResponseUserDTO.class);
    }

    private void checkForEmptyField(String field) {
        if (field == null || field.isBlank()) {
            throw new BadRequestException("Empty row");
        }
    }

    private void validationUsername(String username) {
        // What characters can I use in my TikTok username?
        // Your username can only contain letters, numbers, underscores, and periods.
        // There is also a 24-character limit.
        // nonbinary string comparisons are case-insensitive by default
        // nonbinary strings (CHAR, VARCHAR, TEXT)

//        checkForEmptyField(username);
//        if (username.length() > 24) {
//            throw new BadRequestException("Username length should be maximum 24 symbols");
//        }

//        String validUserName = "^[A-Za-z\\d._]{1,24}$";
//        Pattern p = Pattern.compile(validUserName);
//        Matcher m = p.matcher(username);
//        if (!m.find()) {
//            throw new BadRequestException("Invalid username, only contain letters, numbers, underscores, and periods");
//        }
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

    private void validationPassword(String pass, String confirmPass) {

        //checkForEmptyField(password);
        //checkForEmptyField(confirmPassword);

//        if (password.length() < 8 || password.length() > 20) {
//            throw new BadRequestException("The size length should be between 8 and 20 symbols");
//        }

        if (!pass.equals(confirmPass)) {
            throw new BadRequestException("Password and confirm pass are not the same.");
        }

//        String validPassword = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$";
//
//        // at least one digit [0-9]
//        // at least one lowercase Latin character [a-z]
//        // at least one uppercase Latin character [A-Z]
//        // at least one special character
//        // length: 8-20 symbols
//
//        Pattern p = Pattern.compile(validPassword);
//        Matcher m = p.matcher(pass);
//
//        if (!m.find()) {
//            throw new BadRequestException("Password should contain at least one special symbol, at least one digit, " +
//                    "at least one capital && at least one small letter.");
//        }
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

    public void deleteUser(int userId) {
        User u = getUserById(userId);
        u.setUsername("Delete on " + LocalDateTime.now());
        u.setPassword("Delete on " + LocalDateTime.now());
        u.setFirstName("Delete on " + LocalDateTime.now());
        u.setLastName("Delete on " + LocalDateTime.now());
        u.setEmail("Delete on " + LocalDateTime.now());
        u.setPhoneNumber("Delete on" + u.getId());
        u.setPhotoURL("DELETED ON " + LocalDateTime.now());
        userRepository.save(u);
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

    public ChangePassResponseUserDTO changePass(int userIdFromSession,
                                                ChangePassRequestUserDTO dto) {
        if (dto.getCurrentPassword().equals(dto.getNewPassword())) {
            throw new BadRequestException("Current pass and new pass are the same.");
        }
        validationPassword(dto.getNewPassword(), dto.getConfirmNewPassword());
        User u = getUserById(userIdFromSession);
        // encode password
        dto.setCurrentPassword(DigestUtils.sha256Hex(dto.getCurrentPassword()));
        if (!dto.getCurrentPassword().equals(u.getPassword())) {
            throw new BadRequestException("Invalid password!");
        }
        u.setPassword(DigestUtils.sha256Hex(dto.getNewPassword()));
        userRepository.save(u);
        return modelMapper.map(u, ChangePassResponseUserDTO.class);
    }

    public EditProfilePhotoResponseDTO uploadProfilePhoto(int userIdFromSession,
                                                          MultipartFile file) {
        try {
            User user = getUserById(userIdFromSession);
            checkContentType(file);
            checkSizePhoto(file.getInputStream());
            String ext = FilenameUtils.getExtension(file.getOriginalFilename());
            String name = "photo" + File.separator + System.nanoTime() + "." + ext;
            File f = new File(name);
            if (!f.exists()) {
                Files.copy(file.getInputStream(), f.toPath());
            } else {
                throw new BadRequestException("The file already exists.");
            }
            if (user.getPhotoURL() != null) {
                File old = new File(user.getPhotoURL());
                old.delete();
            }
            user.setPhotoURL(name);
            userRepository.save(user);
            return modelMapper.map(user, EditProfilePhotoResponseDTO.class);
        } catch (IOException e) {
            throw new BadRequestException(e.getMessage(), e);
        }
    }

    private void checkContentType(MultipartFile file) {
        if (!file.getContentType().contains("image/jpeg")
                && !file.getContentType().contains("image/jpg")
                && !file.getContentType().contains("image/png")) {
            throw new BadRequestException("Photo must be .jpg, .jpeg or .png format.");
        }
    }

    private void checkSizePhoto(InputStream inputStreamFile) throws IOException {
        BufferedImage buffImage = ImageIO.read(inputStreamFile);
        int width = buffImage.getWidth();
        int height = buffImage.getHeight();
        if (width < 20 || height < 20) {
            throw new BadRequestException("Photos must be at least 20x20 pixels to upload");
        }
    }
}

