package com.tiktok.service;

import com.tiktok.model.dto.videoDTO.*;
import com.tiktok.model.entities.User;
import com.tiktok.model.entities.Video;
import com.tiktok.model.exceptions.BadRequestException;
import com.tiktok.model.exceptions.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class VideoService extends GlobalService {

    @Autowired
    protected FileService fileService;

    @Transactional(rollbackOn = {SQLException.class}) // cause the sound and hashtag should be made as well
    public PostResponsVideoDTO uploadVideo(int userId, MultipartFile file, Boolean isLive, Boolean isPrivate, String description) {
        User user = getUserById(userId);
        String path = fileService.createVideo(file, userId);
        Video video = new Video();
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
        return modelMapper.map(video, PostResponsVideoDTO.class);
    }
    @Transactional(rollbackOn = {SQLException.class})
    public EditResponseVideoDTO editVideo(int videoId, EditRequestVideoDTO dto, int userId) {
        Video video = getVideoById(videoId);
        User user = getUserById(userId); //check if the user is in DB
        confirmVideoOwner(userId, video);
        video.setPrivate(dto.isPrivate());
        video.setDescription(dto.getDescription());
        if (!video.isPrivate()) {
            //todo create a sound
            //todo set the sound id in video
        }
        videoRepository.save(video);
        return modelMapper.map(video, EditResponseVideoDTO.class);
    }

    public String deleteVideo(int videoId, int userId) {
        Video video = getVideoById(videoId);
        User user = getUserById(userId); //check if the user is in DB
        confirmVideoOwner(userId, video);
        if (video.getVideoUrl() != null) {
            File currentVideo = new File(video.getVideoUrl());
            currentVideo.delete();
        }
        videoRepository.delete(video);
        return "The video is deleted";
    }

    public String likeVideo(int videoId, int userId) {
        Video video = getVideoById(videoId);
        User user = getUserById(userId);
        if (video.isPrivate() || !confirmVideoOwner(userId, video)) { //even the owner can't like the video
            throw new UnauthorizedException("The video is locked by owner");
        }
        if (user.getLikedVideos().contains(video)) {
            user.getLikedVideos().remove(video);
        } else {
            user.getLikedVideos().add(video);
        }
        userRepository.save(user);
        return "Video has " + video.getLikers().size() + " likes.";
    }

    public List<VideoWithoutOwnerDTO> showMyVideos(int userId, int pageNumber, int videosPerPage){
        Pageable page = PageRequest.of(pageNumber, videosPerPage);
        User user = getUserById(userId);
        List<Video> videos = videoRepository.findMyVideos(userId, page);
        List<VideoWithoutOwnerDTO> myVideos = new ArrayList<>();
        for (Video v : videos) {
            VideoWithoutOwnerDTO dto = modelMapper.map(v, VideoWithoutOwnerDTO.class);
            myVideos.add(dto);
        }
        return myVideos;
    }


    private boolean confirmVideoOwner(int userId, Video video) {
        List<Video> myVideos = videoRepository.findAllByOwner(getUserById(userId));
        for (Video v : myVideos) {
            if (v.getId() == video.getId()) {
                return true;
            }
        }
        throw new BadRequestException("The video you tried to deleted is not yours");
    }


    public List<VideoWithoutOwnerDTO> showByKrasiRequst(RequestShowByDTO dto) { // todo convert the date Incorrect DATETIME value: ':=uploadAt'
        String uploadAt = dto.getUploadAt();
        String uploadTo = dto.getUploadTo();
        List<Video> videos = videoRepository.KrasiRequest(dto.getTitle(), dto.getUsername(),
                uploadAt, uploadTo);
        List<VideoWithoutOwnerDTO> krasiResponse = new ArrayList<>();
        for (Video v : videos) {
            VideoWithoutOwnerDTO krasi = modelMapper.map(v, VideoWithoutOwnerDTO.class);
            krasiResponse.add(krasi);
        }
        return krasiResponse;
    }
}
