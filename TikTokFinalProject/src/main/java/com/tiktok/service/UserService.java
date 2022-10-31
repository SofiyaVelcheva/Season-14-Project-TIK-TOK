package com.tiktok.service;

import com.tiktok.model.dto.user.*;
import com.tiktok.model.dto.TextResponseDTO;
import com.tiktok.model.entities.User;
import com.tiktok.model.exceptions.BadRequestException;
import com.tiktok.model.exceptions.NotFoundException;
import com.tiktok.model.exceptions.UnauthorizedException;
import net.bytebuddy.utility.RandomString;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class UserService extends GlobalService {

    @Autowired
    private JavaMailSender mailSender;
    private static final int MIN_YEARS_USER = 13;
    private static final int MAX_YEARS_USER = 100;

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
        user.setVerificationCode(RandomString.make(6));
        userRepository.save(user);
        try {
            sendVerificationEmail(user);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
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
        u.setVerificationCode("Deleted on " + LocalDateTime.now());
        u.setVerifiedEmail(false);
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
        return users.stream().map(u -> modelMapper.map(u, PublisherUserDTO.class)).collect(Collectors.toList());
    }

    public TextResponseDTO verifyEmail(String verificationCode) {
        int id = 0;
        Pattern pattern = Pattern.compile("(?<=@)(.*?)(?=@)");
        Matcher matcher = pattern.matcher(verificationCode);
        if (matcher.find()) {
            id = Integer.parseInt(matcher.group(1));
        }
        User user = getUserById(id);
        user.setVerifiedEmail(true);
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom("tiktokteams14itt@gmail.com");
        msg.setTo(user.getEmail());
        msg.setSubject("Verified");
        msg.setText("You have verified your account");
        mailSender.send(msg);
        userRepository.save(user);
        return new TextResponseDTO("Dear " + user.getFirstName() + " " + user.getLastName() + " Your account has been verified!");
    }

    private void sendVerificationEmail(User user) throws MessagingException, UnsupportedEncodingException {
        String token = System.currentTimeMillis() + "$$$" + new Random().nextInt(99999) + "*=3214@" + user.getId() + "@" + System.currentTimeMillis();
        String subject = "Tik-Tok - Verify your registration";
        String content = "Dear [[name]],"
                + "\nYou have to verify tour account.\\"
                + "\nPlease follow this link: http://localhost:6969/users/verifyEmail/" + token
                + "\nThank you!,"
                + "\nTik Tok Team";
        sendEmail(user, subject, content);
    }

    public void sendEmail(User user, String subject, String content1) throws MessagingException, UnsupportedEncodingException {
        new Thread(() -> {
            String toAddress = user.getEmail();
            String fromAddress = "tiktokteams14itt@gmail.com";
            String senderName = "Tik Tok Team";
            String content = content1;
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            try {
                helper.setFrom(fromAddress, senderName);
                helper.setTo(toAddress);
                helper.setSubject(subject);
                content = content.replace("[[name]]", user.getFirstName() + " " + user.getLastName());
                helper.setText(content, true);
            } catch (MessagingException | UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            mailSender.send(message);
        }).start();
    }

    public boolean verifyAccount(int userId) {
        User user = getUserById(userId);
        return user.isVerifiedEmail();
    }
}

