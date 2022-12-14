package com.tiktok.service;


import com.tiktok.model.dto.TextResponseDTO;
import com.tiktok.model.dto.video.response.VideoRequestEditDTO;
import com.tiktok.model.dto.video.response.EditResponseVideoDTO;
import com.tiktok.model.dto.video.response.VideoResponseDTO;
import com.tiktok.model.dto.video.response.VideoResponseUploadDTO;
import com.tiktok.model.dto.video.response.VideoResponseWithoutOwnerDTO;
import com.tiktok.model.entities.User;
import com.tiktok.model.entities.Video;
import com.tiktok.model.exceptions.BadRequestException;
import com.tiktok.model.exceptions.NotFoundException;
import com.tiktok.model.exceptions.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
public class VideoService extends GlobalService {

    public static final int MAX_LENGTH = 200;
    @Autowired
    private FileService fileService;

    @Autowired
    private SoundService soundService;
    @Autowired
    private HashtagService hashtagService;


    @Transactional // cause the sound and hashtag should be made as well
    public VideoResponseDTO uploadVideo(int userId, MultipartFile file, Boolean isLive, Boolean isPrivate, String description) {
        if (description.length() > MAX_LENGTH) {
            throw new BadRequestException("The description is too long!");
        }
        User user = getUserById(userId);
        String path = fileService.createVideo(file, userId);
        Video video = new Video();
        video.setUploadAt(LocalDateTime.now());
        video.setOwner(user);
        video.setVideoUrl(path);
        video.setLive(isLive);
        video.setPrivate(isPrivate);
        video.setDescription(description);
        hashtagService.addHashtags(video);
        videoRepository.save(video);
        if (!video.isPrivate()) {
            //soundService.newSound(video, path);
        }
        return modelMapper.map(video, VideoResponseDTO.class);
    }

    @Transactional
    public EditResponseVideoDTO editVideo(int videoId, VideoRequestEditDTO dto, int userId) {
        if (dto.getDescription().length() > MAX_LENGTH) {
            throw new BadRequestException("The description is too long!");
        }
        Video video = getVideoById(videoId);
        validateVideoWithOwner(userId, videoId, "The video you try to edit is not yours");
        video.setPrivate(dto.isPrivate());
        video.getHashtags().clear();
        video.setDescription(dto.getDescription());
        hashtagService.addHashtags(video);
        if (!video.isPrivate()) {
            //soundService.newSound(video, path);
        }
        videoRepository.save(video);
        return modelMapper.map(video, EditResponseVideoDTO.class);
    }

    public TextResponseDTO likeVideo(int videoId, int userId) {
        Video video = getVideoById(videoId);
        User user = getUserById(userId);
        if (video.getOwner().equals(user)) {
            throw new UnauthorizedException("The video you try to like is yours!"); //even the owner can't like the video
        }
        if (user.getLikedVideos().contains(video)) {
            user.getLikedVideos().remove(video);
        } else {
            user.getLikedVideos().add(video);
        }
        userRepository.save(user);
        return new TextResponseDTO("Video has " + video.getLikers().size() + " likes.");
    }

    @Transactional
    public TextResponseDTO deleteVideo(int videoId, int userId) {
        Video video = getVideoById(videoId);
        validateVideoWithOwner(userId, videoId, "The video you try to delete is not yours");
        if (video.getVideoUrl() != null) {
            File currentVideo = new File(video.getVideoUrl());
            currentVideo.delete();
        }
        videoRepository.delete(video);
        return new TextResponseDTO("The video is deleted!");
    }

    public List<VideoResponseWithoutOwnerDTO> showMyVideos(int userId, int pageNumber, int videosPerPage) {
        pageable = PageRequest.of(pageNumber, videosPerPage);
        List<Video> videos = videoRepository.showMyVideos(userId, pageable);
        return mapDto(videos);
    }

    public List<VideoResponseWithoutOwnerDTO> showLiveVideos(int pageNumber, int videosPerPage) {
        pageable = PageRequest.of(pageNumber, videosPerPage);
        List<Video> videos = videoRepository.getAllLiveVideos(pageable);
        return mapDto(videos);
    }

    private void validateVideoWithOwner(int userId, int videoId, String errorMessage) {
        List<Video> myVideos = videoRepository.findAllByOwner(getUserById(userId));
        for (Video video : myVideos) {
            if (video.getId() == videoId) {
                return;
            }
        }
        throw new UnauthorizedException(errorMessage);
    }

    private List<VideoResponseWithoutOwnerDTO> mapDto(List<Video> videos) {
        List<VideoResponseWithoutOwnerDTO> dtoVideos = new ArrayList<>();
        for (Video video : videos) {
            VideoResponseWithoutOwnerDTO dto = modelMapper.map(video, VideoResponseWithoutOwnerDTO.class);
            dtoVideos.add(dto);
        }
        return dtoVideos;
    }

    public List<VideoResponseUploadDTO> getAllVideosHashtag(String text, int page, int perPage) {
        pageable = PageRequest.of(page, perPage);
        text = text.startsWith("#") ? text : "#" + text;
        text = "%" + text + "%";
        List<Video> allVideos = videoRepository.getAllHashtagByName(text, pageable);
        if (allVideos.isEmpty()) {
            throw new NotFoundException("Not videos found with this hashtag.");
        }
        return getVideoUploadResponseDTOS(allVideos);
    }

    public List<VideoResponseUploadDTO> getVideosPublishers(int userId, int page, int perPage) {
        pageable = PageRequest.of(page, perPage);
        List<Video> videos = videoRepository.getAllVideosPublishers(userId, pageable);
        return getVideoUploadResponseDTOS(videos);
    }

    public List<VideoResponseWithoutOwnerDTO> showVideosHomePage(int pageNumber, int videosPerPage) {
        pageable = PageRequest.of(pageNumber, videosPerPage);
        List<Video> videos = videoRepository.showAllVideosByUploadAt(pageable);
        return mapDto(videos);
    }
}
