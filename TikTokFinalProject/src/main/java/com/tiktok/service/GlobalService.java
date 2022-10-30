package com.tiktok.service;

import com.tiktok.model.dto.user.PublisherUserDTO;
import com.tiktok.model.dto.user.UserResponseDTO;
import com.tiktok.model.dto.video.response.VideoResponseUploadDTO;
import com.tiktok.model.entities.Comment;
import com.tiktok.model.entities.Message;
import com.tiktok.model.entities.User;
import com.tiktok.model.entities.Video;
import com.tiktok.model.exceptions.BadRequestException;
import com.tiktok.model.exceptions.NotFoundException;
import com.tiktok.model.repository.*;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;


public abstract class GlobalService {

    @Autowired
    protected ModelMapper modelMapper;
    @Autowired
    protected VideoRepository videoRepository;
    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected SoundRepository soundRepository;
    @Autowired
    protected CommentRepository commentRepository;
    @Autowired
    protected MessageRepository messageRepository;
    @Autowired
    @Setter
    protected HashtagRepository hashtagRepository;

    protected Video getVideoById(int videoId) {
        return videoRepository.findById(videoId).orElseThrow(() -> new NotFoundException("Video not found"));
    }

    protected User getUserById(int userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found."));
    }

    protected Comment getCommentById(int commentId) {
        return commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException("Comment not found"));
    }

    protected Message getMessageById(int messageID) {
        return messageRepository.findById(messageID).orElseThrow(() -> new NotFoundException("Message not found."));
    }

    protected User getUserByUsernamePassword(String username, String password) {
        return userRepository.findByUsernameAndPassword(username, password).orElseThrow(() -> new BadRequestException("Invalid username or password!"));
    }

    protected List<VideoResponseUploadDTO> getVideoUploadResponseDTOS(List<Video> allVideos) {
        List<VideoResponseUploadDTO> responseVideos = new ArrayList<>();
        for (Video video : allVideos) {
            VideoResponseUploadDTO dto = modelMapper.map(video, VideoResponseUploadDTO.class);
            dto.setNumberOfLikes(video.getLikers().size());
            dto.setNumberOfComments(video.getComments().size());
            dto.setPublisher(modelMapper.map(video.getOwner(), PublisherUserDTO.class));
            responseVideos.add(dto);
        }
        return responseVideos;
    }
    public UserResponseDTO getUserResponseDTO(User u) {
        UserResponseDTO responseDTO = modelMapper.map(u, UserResponseDTO.class);
        if (u.getVideos() != null) {
            responseDTO.setNumberOfVideos(u.getVideos().size());
        }
        if (u.getSubscribers() != null) {
            responseDTO.setNumberOfSubscribers(u.getSubscribers().size());
        }
        if (u.getSubscribeTo() != null) {
            responseDTO.setNumberOfSubscribeTo(u.getSubscribeTo().size());
        }
        return responseDTO;
    }
}
