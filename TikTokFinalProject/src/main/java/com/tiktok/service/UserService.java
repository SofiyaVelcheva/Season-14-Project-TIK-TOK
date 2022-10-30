package com.tiktok.service;

import com.tiktok.model.dto.user.*;
import com.tiktok.model.entities.User;
import com.tiktok.model.exceptions.BadRequestException;
import com.tiktok.model.exceptions.NotFoundException;
import com.tiktok.model.exceptions.UnauthorizedException;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService extends GlobalService {
    private static final int MIN_YEARS_USER = 13;
    private static final int MAX_YEARS_USER = 100;
    private static Pageable pageable;

    public UserResponseDTO login(LoginRequestUserDTO dto) {
        dto.setPassword(DigestUtils.sha256Hex(dto.getPassword()));
        User u = getUserByUsernamePassword(dto.getUsername(), dto.getPassword());
        u.setLastLogin(LocalDateTime.now());
        userRepository.save(u);
        return getUserResponseDTO(u);
    }

    public UserResponseDTO register(RegisterRequestUserDTO dto) {
        validateUsername(dto.getUsername());
        validatePassword(dto.getPassword(), dto.getConfirmPassword());
        validateEmail(dto.getEmail());
        validatePhone(dto.getPhoneNumber());
        validateBirthday(dto.getDateOfBirth());
        dto.setPassword(DigestUtils.sha256Hex(dto.getPassword()));
        User user = modelMapper.map(dto, User.class);
        user.setVerifiedEmail(false);
        user = userRepository.save(user);
        return getUserResponseDTO(user);
    }

    private void validateUsername(String username) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new BadRequestException("User with this username already exists.");
        }
    }

    private void validatePassword(String pass, String confirmPass) {
        if (!pass.equals(confirmPass.trim())) {
            throw new BadRequestException("Passwords don't match.");
        }
    }

    private void validatePhone(String phoneNumber) {
        if (userRepository.findByPhoneNumber(phoneNumber).isPresent()) {
            throw new BadRequestException("User with this phone number already exists.");
        }
    }

    private void validateEmail(String email) {
        if (userRepository.findByEmail(email.trim()).isPresent()) {
            throw new BadRequestException("User with this email already exists.");
        }

    }

    private void validateBirthday(LocalDate dateOfBirth) {
        Period p = Period.between(dateOfBirth, LocalDate.now());
        if (p.getYears() < MIN_YEARS_USER || p.getYears() > MAX_YEARS_USER) {
            throw new UnauthorizedException("You should be at least 13 years old.");
        }
    }

    public void deleteUser(int userId, int userIdFromSession) {
        if (userId != userIdFromSession) {
            throw new BadRequestException("You are not the owner of this account.");
        }
        User u = getUserById(userId);
        if (u.getUsername().contains("Delete")) {
            throw new NotFoundException("User not found.");
        }
        u.setUsername("Deleted on " + LocalDateTime.now());
        u.setPassword("Deleted on " + LocalDateTime.now());
        u.setFirstName("Deleted on " + LocalDateTime.now());
        u.setLastName("Deleted on " + LocalDateTime.now());
        u.setEmail("Deleted on " + LocalDateTime.now());
        u.setPhoneNumber("Deleted on " + u.getId());
        u.setPhotoURL("Deleted on  " + LocalDateTime.now());
        userRepository.save(u);
    }

    public UserResponseDTO edit(int id, EditUserRequestDTO dto) {
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
        return getUserResponseDTO(u);
    }

    public UserResponseDTO changePass(int userIdFromSession, ChangePassRequestUserDTO dto) {
        if (dto.getCurrentPassword().equals(dto.getNewPassword())) {
            throw new BadRequestException("Current pass and new pass are the same.");
        }
        validatePassword(dto.getNewPassword(), dto.getConfirmNewPassword());
        User u = getUserById(userIdFromSession);
        dto.setCurrentPassword(DigestUtils.sha256Hex(dto.getCurrentPassword()));
        if (!dto.getCurrentPassword().equals(u.getPassword())) {
            throw new BadRequestException("Invalid password!");
        }
        u.setPassword(DigestUtils.sha256Hex(dto.getNewPassword()));
        userRepository.save(u);
        return getUserResponseDTO(u);
    }



    public void subscribe(int publisherId, int subscriberId) {
        if (publisherId == subscriberId) {
            throw new BadRequestException("You can not subscribe yourself.");
        }
        User publisher = getUserById(publisherId);
        User subscriber = getUserById(subscriberId);
        if (publisher.getSubscribers().contains(subscriber)) {
            throw new BadRequestException("You are already subscribed to that person.");
        }
        publisher.getSubscribers().add(subscriber);
        userRepository.save(publisher);
    }

    public void unsubscribe(int publisherId, int subscriberId) {
        if (publisherId == subscriberId) {
            throw new BadRequestException("You can not unsubscribe yourself.");
        }
        User publisher = getUserById(publisherId);
        User subscriber = getUserById(subscriberId);
        if (!publisher.getSubscribers().contains(subscriber)) {
            throw new BadRequestException("You are not subscribed to that user.");
        }
        publisher.getSubscribers().remove(subscriber);
        userRepository.save(publisher);
    }

    public UserResponseDTO getUser(int uid) {
        User u = getUserById(uid);
        return getUserResponseDTO(u);
    }

    public List<UserResponseDTO> getAllUsersByUsername(String username, int page, int perPage) {
        if (username.trim().isEmpty()) {
            throw new BadRequestException("Username cannot be empty.");
        }
        username = "%" + username + "%";
        pageable = PageRequest.of(page, perPage);
        List<User> users = userRepository.findAllByUsername(username, pageable);
        List<UserResponseDTO> responseUsers = new ArrayList<>();
        for (User u : users) {
            UserResponseDTO user = getUserResponseDTO(u);
            responseUsers.add(user);
        }
        return responseUsers;
    }

    public List<PublisherUserDTO> getAllMyPublishers(int userId, int page, int perPage) {
        pageable = PageRequest.of(page, perPage);
        List<User> users = userRepository.getAllSub(userId, pageable);
        List<PublisherUserDTO> publisherDTO = users.stream().map(u -> modelMapper.map(u, PublisherUserDTO.class)).collect(Collectors.toList());
        return publisherDTO;
    }


}

