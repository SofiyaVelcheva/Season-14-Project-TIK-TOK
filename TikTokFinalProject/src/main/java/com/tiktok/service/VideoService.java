package com.tiktok.service;

import com.tiktok.model.dto.videoDTO.EditRequestVideoDTO;
import com.tiktok.model.dto.videoDTO.EditResponseVideoDTO;
import com.tiktok.model.dto.videoDTO.VideoWithoutOwnerDTO;
import com.tiktok.model.entities.User;
import com.tiktok.model.entities.Video;
import com.tiktok.model.exceptions.BadRequestException;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class VideoService extends GlobalService {
    @Autowired
    protected SoundService soundService;

    public VideoWithoutOwnerDTO uploadVideo(int userId, MultipartFile file, Boolean isLive, Boolean isPrivate, String description) {
        try {
            User user = getUserById(userId);
            String ext = FilenameUtils.getExtension(file.getOriginalFilename()); //todo only videos not photos
            String path = "videos" + File.separator + System.nanoTime() + "." + ext;
            File newFile = new File(path);
            if (!newFile.exists()) {
                Files.copy(file.getInputStream(), newFile.toPath());
            } else {
                throw new BadRequestException("The file already exists!");
            }
            Video video = new Video();
            if (video.getVideoUrl() != null) {
                File old = new File(video.getVideoUrl());
                old.delete();
            }
            video.setUploadAt(LocalDateTime.now());
            video.setOwner(user);
            video.setVideoUrl(path);
            video.setLive(isLive);
            video.setPrivate(isPrivate);
            video.setDescription(description);
            videoRepository.save(video);
            if (!video.isPrivate()) {
                //todo create a sound
                //soundService.newSound(video, path); //todo fix .ApiException: java.net.SocketTimeoutException: timeout
                //todo set the sound id in video
            }
            return modelMapper.map(video, VideoWithoutOwnerDTO.class);
        } catch (IOException e) {
            throw new BadRequestException(e.getMessage(), e);
        }
    }

    public EditResponseVideoDTO editVideo(int videoId, EditRequestVideoDTO dto) {
        Video video = getVideoById(videoId);
        video.setPrivate(dto.isPrivate()); // todo can be done by modelMapper?
        video.setDescription(dto.getDescription());
        if (!video.isPrivate()) {
            //todo create a sound
            //todo set the sound id in video
        }
        videoRepository.save(video);
        return modelMapper.map(video, EditResponseVideoDTO.class);
    }

    public String deleteVideo(int videoId) {
        Video video = getVideoById(videoId);
        if (video.getVideoUrl() != null) {
            File old = new File(video.getVideoUrl());
            old.delete();
        }
        videoRepository.delete(video);
        return "The video is deleted";
    }

    public String likeVideo(int videoId, int userId) {
        User user = getUserById(userId);
        Video video = getVideoById(videoId);
        if(user.getLikedVideos().contains(video)){
            user.getLikedVideos().remove(video);
        }
        else{
            user.getLikedVideos().add(video);
        }
        userRepository.save(user);
        return "Video has " + video.getLikers().size()+ " likes.";
    }
}
