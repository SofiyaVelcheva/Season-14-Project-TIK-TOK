package com.tiktok.service;

import com.tiktok.model.entities.Comment;
import com.tiktok.model.entities.Message;
import com.tiktok.model.entities.User;
import com.tiktok.model.entities.Video;
import com.tiktok.model.exceptions.NotFoundException;
import com.tiktok.model.exceptions.UnauthorizedException;
import com.tiktok.model.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.List;

@RestController
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


    protected Video getVideoById(int videoId) {
        return videoRepository.findById(videoId).orElseThrow(() -> new NotFoundException("Video not found"));
    }

    protected User getUserById(int userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
    }

    protected Comment getCommentById(int commentId) {
        return commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException("Comment not found"));
    }

    protected Message getMessageById(int messageID){
        return messageRepository.findById(messageID).orElseThrow(() -> new NotFoundException("Message not found"));
    }

    protected void checkCollection(List<?> collection) {
        if (collection.isEmpty()){
            throw new UnauthorizedException("Not found suggested");
        }
    }


}
