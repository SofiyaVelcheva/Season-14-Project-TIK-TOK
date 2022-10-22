package com.tiktok.service;

import com.tiktok.model.dto.userDTO.*;
import com.tiktok.model.entities.User;
import com.tiktok.model.exceptions.BadRequestException;
import com.tiktok.model.exceptions.NotFoundException;
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
        validateUsername(dto.getUsername());
        validatePassword(dto.getPassword(), dto.getConfirmPassword());
        validateEmail(dto.getEmail());
        validatePhone(dto.getPhoneNumber());
        validateBirthday(dto.getDateOfBirth());
        // encode password
        dto.setPassword(DigestUtils.sha256Hex(dto.getPassword()));
        User user = modelMapper.map(dto, User.class);
        user.setVerifiedEmail(false);
        userRepository.save(user);
        return modelMapper.map(dto, RegisterResponseUserDTO.class);
    }

    private void validateUsername(String username) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new BadRequestException("User with this username already exist.");
        }
    }

    private void validatePassword(String pass, String confirmPass) {
        if (!pass.equals(confirmPass)) {
            throw new BadRequestException("Password and confirm pass are not the same.");
        }
    }

    private void validatePhone(String phoneNumber) {
        if (userRepository.findByPhoneNumber(phoneNumber).isPresent()) {
            throw new BadRequestException("User with this phone number already exist.");
        }
    }

    private void validateEmail(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new BadRequestException("User with this email already exist.");
        }

    }

    private void validateBirthday(LocalDate dateOfBirth) {
        Period p = Period.between(dateOfBirth, LocalDate.now());
        if (p.getYears() < 13 || p.getYears() > 100) {
            throw new UnauthorizedException("You should be more 13 years old.");
        }
    }

    public void deleteUser(int userId) {
        User u = getUserById(userId);
        if (u.getUsername().contains("delete")) {
            throw new NotFoundException("User not found");
        }
        u.setUsername("Delete on " + LocalDateTime.now());
        u.setPassword("Delete on " + LocalDateTime.now());
        u.setFirstName("Delete on " + LocalDateTime.now());
        u.setLastName("Delete on " + LocalDateTime.now());
        u.setEmail("Delete on " + LocalDateTime.now());
        u.setPhoneNumber("Delete on" + u.getId());
        u.setPhotoURL("Delete on  " + LocalDateTime.now());
        userRepository.save(u);
    }

    public EditUserResponseDTO edit(int id, EditUserRequestDTO dto) {
        User u = getUserById(id);
        if (dto.getFirstName() != null) {
            u.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null) {
            u.setLastName(dto.getLastName());
        }
        if (dto.getEmail() != null) {
            validateEmail(dto.getEmail());
            u.setEmail(dto.getEmail());
        }
        if (dto.getPhoneNumber() != null) {
            validatePhone(dto.getPhoneNumber());
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
        validatePassword(dto.getNewPassword(), dto.getConfirmNewPassword());
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

