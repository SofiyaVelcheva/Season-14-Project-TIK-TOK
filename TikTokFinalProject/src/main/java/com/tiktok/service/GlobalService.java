package com.tiktok.service;

import com.tiktok.model.dto.userDTO.PublisherUserDTO;
import com.tiktok.model.dto.videoDTO.VideoUploadResponseDTO;
import com.tiktok.model.entities.Comment;
import com.tiktok.model.entities.Message;
import com.tiktok.model.entities.User;
import com.tiktok.model.entities.Video;
import com.tiktok.model.exceptions.BadRequestException;
import com.tiktok.model.exceptions.NotFoundException;
import com.tiktok.model.exceptions.UnauthorizedException;
import com.tiktok.model.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

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
    protected HashtagRepository hashtagRepository;

    protected Video getVideoById(int videoId){
        return videoRepository.findById(videoId).orElseThrow(() -> new NotFoundException("Video not found"));
    }
    protected User getUserById(int userId){
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
    }

    protected Comment getCommentById(int commentId){
        return commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException("Comment not found"));
    }

    protected Message getMessageById(int messageID){
        return messageRepository.findById(messageID).orElseThrow(() -> new NotFoundException("Message not found"));
    }

    protected void checkCollection(Collection<?> collection) {
        if (collection.isEmpty()){
            throw new NotFoundException("Not found suggested");
        }
    }

    protected User getUserByUsernamePassword(String username, String password){
        return userRepository.findByUsernameAndPassword(username, password).orElseThrow(() -> new BadRequestException("Username or password invalid!"));
    }

    protected List<VideoUploadResponseDTO> getVideoUploadResponseDTOS(List<Video> allVideos) {
        List<VideoUploadResponseDTO> responseVideos = new ArrayList<>();
        for (Video video : allVideos) {
            VideoUploadResponseDTO dto = modelMapper.map(video, VideoUploadResponseDTO.class);
            dto.setNumberOfLikes(video.getLikers().size());
            dto.setNumberOfComments(video.getComments().size());
            dto.setPublisher(modelMapper.map(video.getOwner(), PublisherUserDTO.class));
            responseVideos.add(dto);
        }
        return responseVideos;
    }

}
