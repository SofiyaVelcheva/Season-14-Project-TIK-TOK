package com.tiktok.service;

import com.tiktok.model.dto.comments.CommentWithoutVideoDTO;
import com.tiktok.model.dto.videoDTO.EditRequestVideoDTO;
import com.tiktok.model.dto.videoDTO.EditResponseVideoDTO;
import com.tiktok.model.dto.videoDTO.VideoWithoutOwnerDTO;
import com.tiktok.model.entities.Comment;
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
import java.util.ArrayList;
import java.util.List;

@Service
public class VideoService extends GlobalService {
    @Autowired
    protected SoundService soundService;

    public VideoWithoutOwnerDTO uploadVideo(int userId, MultipartFile file, Boolean isLive, Boolean isPrivate, String description) {
        try {
            String ext = FilenameUtils.getExtension(file.getOriginalFilename());
            System.out.println(ext);
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
        Video video = getVideoById(videoId);
        List<Comment> comments = commentRepository.findParentCommentsOrderByDate(videoId);
        System.out.println(comments.size());
        List<CommentWithoutVideoDTO> allComments = new ArrayList<>();
        for (Comment comment : comments){
            CommentWithoutVideoDTO dto = modelMapper.map(comment, CommentWithoutVideoDTO.class);
            allComments.add(dto);
        }
        return allComments;
    }


//    public List<VideoWithoutOwnerDTO> showAllByLikes() {
//        List <Video> videos = videoRepository.findAll();
//        List <Video> videByLikers = videoRepository.queryAllByLikers(videos); //todo
//
//        List<VideoWithoutOwnerDTO> allVideosByLikers = new ArrayList<>();
//        for(Video video : videByLikers){
//            VideoWithoutOwnerDTO dto = modelMapper.map(video, VideoWithoutOwnerDTO.class);
//            allVideosByLikers.add(dto);
//        }
//        return allVideosByLikers;
//    }
}
