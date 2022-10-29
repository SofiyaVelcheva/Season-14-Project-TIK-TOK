package com.tiktok.service;

import com.tiktok.model.dto.TextResponseDTO;
import com.tiktok.model.dto.userDTO.*;
import com.tiktok.model.dto.videoDTO.response.VideoResponseUploadDTO;
import com.tiktok.model.entities.User;
import com.tiktok.model.entities.Video;
import com.tiktok.model.exceptions.BadRequestException;
import com.tiktok.model.exceptions.NotFoundException;
import com.tiktok.model.exceptions.UnauthorizedException;
import net.bytebuddy.utility.RandomString;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService extends GlobalService {

    @Autowired
    private JavaMailSender mailSender;
    private static Pageable pageable;

    public LoginResponseUserDTO login(LoginRequestUserDTO dto) {
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

    public BasicUserResponseDTO register(RegisterRequestUserDTO dto) {
        validateUsername(dto.getUsername());
        validatePassword(dto.getPassword(), dto.getConfirmPassword());
        validateEmail(dto.getEmail());
        validatePhone(dto.getPhoneNumber());
        validateBirthday(dto.getDateOfBirth());
        dto.setPassword(DigestUtils.sha256Hex(dto.getPassword()));
        User user = modelMapper.map(dto, User.class);
        user.setVerifiedEmail(false);
        user.setVerificationCode(RandomString.make(6));
        try {
            sendVerificationEmail(user);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        userRepository.save(user);
        return modelMapper.map(dto, BasicUserResponseDTO.class);
    }

    private void validateUsername(String username) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new BadRequestException("User with this username already exist.");
        }
    }

    private void validatePassword(String pass, String confirmPass) {
        if (!pass.equals(confirmPass.trim())) {
            throw new BadRequestException("Password and confirm pass are not the same.");
        }
    }

    private void validatePhone(String phoneNumber) {
        if (userRepository.findByPhoneNumber(phoneNumber).isPresent()) {
            throw new BadRequestException("User with this phone number already exist.");
        }
    }

    private void validateEmail(String email) {
        if (userRepository.findByEmail(email.trim()).isPresent()) {
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
        if (u.getUsername().contains("Delete")) {
            throw new NotFoundException("User not found");
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

    public BasicUserResponseDTO edit(int id, EditUserRequestDTO dto) {
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
        return modelMapper.map(u, BasicUserResponseDTO.class);
    }

    public BasicUserResponseDTO changePass(int userIdFromSession,
                                           ChangePassRequestUserDTO dto) {
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
        return modelMapper.map(u, BasicUserResponseDTO.class);
    }

    public BasicUserResponseDTO uploadProfilePhoto(int userIdFromSession,
                                                   MultipartFile file) {
        try {
            User user = getUserById(userIdFromSession);
            checkContentType(file);
            checkSizePhoto(file.getInputStream());
            String ext = FilenameUtils.getExtension(file.getOriginalFilename());
            String name = userIdFromSession + System.nanoTime() + "." + ext;
            String filePath = "photos" + File.separator + name;
            File f = new File(filePath);
            if (!f.exists()) {
                Files.copy(file.getInputStream(), f.toPath());
            } else {
                throw new BadRequestException("The file already exists.");
            }
            if (user.getPhotoURL() != null) {
                File old = new File("photos" + File.separator + user.getPhotoURL());
                old.delete();
            }
            user.setPhotoURL(name);
            userRepository.save(user);
            return modelMapper.map(user, BasicUserResponseDTO.class);
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

    public void subscribe(int publisherId, int subscriberId) {
        if (publisherId == subscriberId) {
            throw new BadRequestException("You can not subscribed yourself.");
        }
        User publisher = getUserById(publisherId);
        User subscriber = getUserById(subscriberId);
        if (publisher.getSubscribers().contains(subscriber)) {
            throw new BadRequestException("You are already subscribed that person.");
        }
        publisher.getSubscribers().add(subscriber);
        userRepository.save(publisher);
    }

    public void unsubscribe(int publisherId, int subscriberId) {
        if (publisherId == subscriberId) {
            throw new BadRequestException("You can not unsubscribed yourself.");
        }
        User publisher = getUserById(publisherId);
        User subscriber = getUserById(subscriberId);
        if (!publisher.getSubscribers().contains(subscriber)) {
            throw new BadRequestException("You are not subscribed that user.");
        }
        publisher.getSubscribers().remove(subscriber);
        userRepository.save(publisher);
    }

    public WithoutPassResponseUserDTO getUser(int uid) {
        User u = getUserById(uid);
        WithoutPassResponseUserDTO dto = modelMapper.map(u, WithoutPassResponseUserDTO.class);
        dto.setNumberOfVideos(u.getVideos().size());
        dto.setNumberOfSubscribers(u.getSubscribers().size());
        dto.setNumberOfSubscribeTo(u.getSubscribeTo().size());
        return dto;
    }

    public List<UsernameResponseDTO> getAllUsersByUsername(String username, int page, int perPage) {
        if (username.trim().isEmpty()) {
            throw new BadRequestException("Empty field with username.");
        }
        username = "%" + username + "%";
        pageable = PageRequest.of(page, perPage);
        List<User> users = userRepository.findAllByUsername(username, pageable);
        if (users.isEmpty()) {
            throw new UnauthorizedException("Not found suggested");
        }
        List<UsernameResponseDTO> responseUsers = new ArrayList<>();
        for (User u : users) {
            UsernameResponseDTO user = modelMapper.map(u, UsernameResponseDTO.class);
            user.setNumberOfSubscribers(u.getSubscribers().size());
            user.setNumberOfVideos(u.getVideos().size());
            responseUsers.add(user);
        }
        return responseUsers;
    }

    public List<VideoResponseUploadDTO> getVideosPublishers(int userId, int page, int perPage) {
        pageable = PageRequest.of(page, perPage);
        List<Video> videos = videoRepository.getAllVideosPublishers(userId, pageable);
        ;
        checkCollection(videos);
        List<VideoResponseUploadDTO> responseVideos = new ArrayList<>();
        for (Video video : videos) {
            VideoResponseUploadDTO dto = modelMapper.map(video, VideoResponseUploadDTO.class);
            //dto.setNumberOfLikes(video.getLikers().size());
            dto.setNumberOfComments(video.getComments().size());
            dto.setPublisher(modelMapper.map(video.getOwner(), PublisherUserDTO.class));
            responseVideos.add(dto);
        }
        return responseVideos;
    }

    public List<PublisherUserDTO> getAllMyPublishers(int userId, int page, int perPage) {
        pageable = PageRequest.of(page, perPage);
        List<User> users = userRepository.getAllSub(userId, pageable);
        checkCollection(users);
        List<PublisherUserDTO> publisherDTO = users.stream().map(u -> modelMapper.map(u, PublisherUserDTO.class)).collect(Collectors.toList());
        return publisherDTO;
    }

    public TextResponseDTO verifyEmail(VerifyEmailDTO dto, int userId) {
        User user = getUserById(userId);
        System.out.println(userId);
        System.out.println(dto.getVerificationCode());
        System.out.println(user.getVerificationCode());
        if (user.getVerificationCode().equals(dto.getVerificationCode())) {
            if (user.isVerifiedEmail()) {
                throw new BadRequestException("You already verified your email!");
            }
            user.setVerifiedEmail(true);
            userRepository.save(user);
            return new TextResponseDTO("You have been verified successfully");
        }
        throw new BadRequestException("The code you enter isn't correct!");
    }

    private void sendVerificationEmail(User user) throws MessagingException, UnsupportedEncodingException {
        String toAddress = user.getEmail();
        String fromAddress = "tiktokteams14itt@gmail.com";
        String senderName = "Tik Tok Team";
        String subject = "Tik-Tok - Verify your registration";
        String content = "Dear [[name]],<br>"
                + "Please use the generated code below to verify your registration:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">CODE: " + user.getVerificationCode() + " </a></h3>"
                + "Thank you!,<br>"
                + "Tik Tok Team";
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);
        content = content.replace("[[name]]", user.getFirstName() + " " + user.getLastName());
        helper.setText(content, true);
        mailSender.send(message);
    }


}

