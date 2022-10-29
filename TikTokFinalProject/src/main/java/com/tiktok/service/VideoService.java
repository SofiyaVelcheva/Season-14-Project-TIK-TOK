package com.tiktok.service;

import com.tiktok.model.dto.comments.CommentWithoutVideoDTO;
import com.tiktok.model.dto.videoDTO.*;
import com.tiktok.model.entities.Comment;
import com.tiktok.model.entities.User;
import com.tiktok.model.entities.Video;
import com.tiktok.model.exceptions.BadRequestException;
import com.tiktok.model.exceptions.NotFoundException;
import com.tiktok.model.exceptions.UnauthorizedException;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
public class VideoService extends GlobalService {

    @Autowired
    private HashtagService hashtagService;


    public VideoWithoutOwnerDTO uploadVideo(int userId, MultipartFile file, Boolean isLive, Boolean isPrivate, String description) {
        try {
            String ext = FilenameUtils.getExtension(file.getOriginalFilename());
            if (!validateFileType(ext)) {
                throw new BadRequestException("The format of the video is not allowed.");
            }
            User user = getUserById(userId);
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
            hashtagService.addHashtags(video);
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


    public EditResponseVideoDTO editVideo(int videoId, EditRequestVideoDTO dto, int userID) {
        Video video = getVideoById(videoId);
        confirmOwner(userID, video);
        video.setPrivate(dto.isPrivate());
        video.getHashtags().clear();
        video.setDescription(dto.getDescription());
        hashtagService.addHashtags(video);
        if (!video.isPrivate()) {
            //todo create a sound
            //todo set the sound id in video
        }
        videoRepository.save(video);
        return modelMapper.map(video, EditResponseVideoDTO.class);
    }

    public String deleteVideo(int videoId, int userId) {
        Video video = getVideoById(videoId);
        confirmOwner(userId, video);
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
        if (video.isPrivate()) { //even the owner can't like the video
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

    public List<VideoWithoutOwnerDTO> showMyVideos(int userId) {
        User user = getUserById(userId);
        List<Video> videos = videoRepository.findAllByOwner(user);
        List<VideoWithoutOwnerDTO> myVideos = new ArrayList<>();
        for (Video v : videos) {
            VideoWithoutOwnerDTO dto = modelMapper.map(v, VideoWithoutOwnerDTO.class);
            myVideos.add(dto);
            System.out.println(v.getLikers().size());
        }
        return myVideos;
    }

    public List<CommentWithoutVideoDTO> showAllComments(int videoId) {
        Video video = getVideoById(videoId);
        List<Comment> comments = commentRepository.findAllByVideo(video);
        List<CommentWithoutVideoDTO> videoWithComments = new ArrayList<>();
        for (Comment comment : comments) {
            CommentWithoutVideoDTO dto = modelMapper.map(comment, CommentWithoutVideoDTO.class);
            videoWithComments.add(dto);
        }
        return videoWithComments;
    }

    private boolean validateFileType(String ext) {
        //TikTok supports the following video file types: .mp4, .mov, .mpeg, .3gp, .avi
        if (ext.equals("mp4") || ext.equals("mov") || ext.equals("mpeg")
                || ext.equals("3gp") || ext.equals("avi")) {
            return true;
        }
        return false;
    }

    public List<CommentWithoutVideoDTO> showAllCommentsOrderByLastAdd(int videoId) {
        Video video = getVideoById(videoId); // if video exists
        List<Comment> comments = commentRepository.findParentCommentsOrderByDate(videoId);
        System.out.println(comments.size());
        List<CommentWithoutVideoDTO> allComments = new ArrayList<>();
        for (Comment comment : comments) {
            CommentWithoutVideoDTO dto = modelMapper.map(comment, CommentWithoutVideoDTO.class);
            allComments.add(dto);
        }
        return allComments;
    }

//    public List<VideoWithoutOwnerDTO> showAllByLikes() {
//        List<Video> videos = videoRepository.findAll();
//        Collections.sort(videos, (o1, o2) -> o2.getLikers().size() - o1.getLikers().size());
//        List<VideoWithoutOwnerDTO> allVideosByLikers = new ArrayList<>();
//        for (Video video : videos) {
//            VideoWithoutOwnerDTO dto = modelMapper.map(video, VideoWithoutOwnerDTO.class);
//            allVideosByLikers.add(dto);
//        }
//        return allVideosByLikers;
//    }
//
//    public List<VideoWithoutOwnerDTO> showAllByComments() {
//        List<Video> videos = videoRepository.findAll();
//        Collections.sort(videos, (o1, o2) -> o2.getComments().size() - o1.getComments().size());
//        List<VideoWithoutOwnerDTO> allVideosByComments = new ArrayList<>();
//        for (Video video : videos) {
//            VideoWithoutOwnerDTO dto = modelMapper.map(video, VideoWithoutOwnerDTO.class);
//            allVideosByComments.add(dto);
//        }
//        return allVideosByComments;
//    }
//
//    public List<VideoWithoutOwnerDTO> showAllByDate() {
//        List<Video> videos = videoRepository.findAll();
//        Collections.sort(videos, (o1, o2) -> o2.getUploadAt().compareTo(o1.getUploadAt()));
//        List<VideoWithoutOwnerDTO> allVideosByDate = new ArrayList<>();
//        for (Video video : videos) {
//            VideoWithoutOwnerDTO dto = modelMapper.map(video, VideoWithoutOwnerDTO.class);
//            allVideosByDate.add(dto);
//        }
//        return allVideosByDate;
//    }

    private void confirmOwner(int userId, Video video) {
        List<Video> myVideos = videoRepository.findAllByOwner(getUserById(userId));
        boolean isMineVideo = false;
        for (Video v : myVideos) {
            if (v.getId() == video.getId()) {
                isMineVideo = true;
                break;
            }
        }
        if (!isMineVideo) {
            throw new BadRequestException("The video you tried to deleted is not yours");
        }
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

    public List<VideoUploadResponseDTO> getAllVideosHashtag(String text, int page, int perPage) {
        Pageable pageable = PageRequest.of(page, perPage);
        text = text.startsWith("#") ? text : "#" + text;
        text = "%" + text + "%";
        List<Video> allVideos = videoRepository.getAllHashtagByName(text, pageable);
        if (allVideos.isEmpty()) {
            throw new NotFoundException("Not found videos with this hashtag.");
        }
        return getVideoUploadResponseDTOS(allVideos);
    }

    public List<VideoUploadResponseDTO> getVideosPublishers(int userId, int page, int perPage) {
        Pageable pageable = PageRequest.of(page, perPage);
        List<Video> videos = videoRepository.getAllVideosPublishers(userId, pageable);
        checkCollection(videos);
        return getVideoUploadResponseDTOS(videos);
    }
}
