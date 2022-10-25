package com.tiktok.service;

import com.tiktok.model.dto.userDTO.*;
import com.tiktok.model.dto.videoDTO.VideoUploadResponseDTO;
import com.tiktok.model.entities.User;
import com.tiktok.model.entities.Video;
import com.tiktok.model.exceptions.BadRequestException;
import com.tiktok.model.exceptions.NotFoundException;
import com.tiktok.model.exceptions.UnauthorizedException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService extends GlobalService {

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

    public BasicUserResponseDTO register(RegisterRequestUserDTO dto) {
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
        // encode password
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
            String filePath = "photo" + File.separator + name;
            File f = new File(filePath);
            if (!f.exists()) {
                Files.copy(file.getInputStream(), f.toPath());
            } else {
                throw new BadRequestException("The file already exists.");
            }
            if (user.getPhotoURL() != null) {
                File old = new File("photo" + File.separator + user.getPhotoURL());
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

    public List<UsernameResponseDTO> getAllUserByUsername(String username, int page, int perPage) {
        if (username.trim().isEmpty()) {
            throw new BadRequestException("Empty field with username.");
        }
        username = "%" + username + "%";
        Pageable pageable = PageRequest.of(page, perPage);
        List<User> users = userRepository.findAllByUsername(username, pageable);
        if (users.isEmpty()){
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

    public List<VideoUploadResponseDTO> getAllPublisher(int userId, int page, int perPage) {
        Pageable pageable = PageRequest.of(page, perPage);
        List<Video> videos = videoRepository.getAllVideosPublishers(userId, pageable);;
        if (videos.isEmpty()){
            throw new UnauthorizedException("Not found suggested");
        }
        List<VideoUploadResponseDTO> responseVideos = new ArrayList<>();
        for (Video video : videos) {
            VideoUploadResponseDTO dto = modelMapper.map(video, VideoUploadResponseDTO.class);
            //dto.setNumberOfLikes(video.getLikers().size());
            dto.setNumberOfComments(video.getComments().size());
            dto.setPublisher(modelMapper.map(video.getOwner(), PublisherUserDTO.class));
            responseVideos.add(dto);
        }
        return responseVideos;
    }
}

