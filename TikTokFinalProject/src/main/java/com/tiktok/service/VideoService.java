package com.tiktok.service;

import com.tiktok.model.dto.videoDTO.request.VideoRequestEditDTO;
import com.tiktok.model.dto.videoDTO.request.VideoRequestShowByDTO;
import com.tiktok.model.dto.videoDTO.response.EditResponseVideoDTO;
import com.tiktok.model.dto.videoDTO.response.VideoResponseDTO;
import com.tiktok.model.dto.videoDTO.response.VideoResponseWithoutOwnerDTO;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class VideoService extends GlobalService {

    @Autowired
    protected FileService fileService;

    @Transactional // cause the sound and hashtag should be made as well
    public VideoResponseDTO uploadVideo(int userId, MultipartFile file, Boolean isLive, Boolean isPrivate, String description) {
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
        return modelMapper.map(video, VideoResponseDTO.class);
    }
    @Transactional
    public EditResponseVideoDTO editVideo(int videoId, VideoRequestEditDTO dto, int userId) {
        Video video = getVideoById(videoId);
        User user = getUserById(userId); //check if the user is in DB
        String errorMessage = "The video you try to edit is not yours";
        validateVideoWithOwner(userId, video, errorMessage);
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
        String errorMessage = "The video you try to delete is not yours";
        validateVideoWithOwner(userId, video, errorMessage);
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
        String errorMessage = "The video you try to like is yours";
        if (video.isPrivate() || !validateVideoWithOwner(userId, video, errorMessage)) { //even the owner can't like the video
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

    public List<VideoResponseWithoutOwnerDTO> showMyVideos(int userId, int pageNumber, int videosPerPage){
        Pageable page = PageRequest.of(pageNumber, videosPerPage);
        User user = getUserById(userId);
        List<Video> videos = videoRepository.findAllByOwner(user);
        List<VideoResponseWithoutOwnerDTO> myVideos = new ArrayList<>();
        for (Video v : videos) {
            VideoResponseWithoutOwnerDTO dto = modelMapper.map(v, VideoResponseWithoutOwnerDTO.class);
            myVideos.add(dto);
            System.out.println(v.getLikers().size());
        }
        return myVideos;
    }


    private boolean validateVideoWithOwner(int userId, Video video, String errorMessage) {
        List<Video> myVideos = videoRepository.findAllByOwner(getUserById(userId));
        for (Video v : myVideos) {
            if (v.getId() == video.getId()) {
                return true;
            }
        }
        throw new BadRequestException(errorMessage);
    }


    public List<VideoResponseWithoutOwnerDTO> showByKrasiRequst(VideoRequestShowByDTO dto) { // todo convert the date Incorrect DATETIME value: ':=uploadAt'
        String uploadAt = dto.getUploadAt();
        String uploadTo = dto.getUploadTo();
        List<Video> videos = videoRepository.KrasiRequest(dto.getTitle(), dto.getUsername(),
                uploadAt, uploadTo);
        List<VideoResponseWithoutOwnerDTO> krasiResponse = new ArrayList<>();
        for (Video v : videos) {
            VideoResponseWithoutOwnerDTO krasi = modelMapper.map(v, VideoResponseWithoutOwnerDTO.class);
            krasiResponse.add(krasi);
        }
        return krasiResponse;
    }
}
