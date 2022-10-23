package com.tiktok.controller;

import com.tiktok.model.dto.comments.CommentWithoutVideoDTO;
import com.tiktok.model.dto.videoDTO.EditRequestVideoDTO;
import com.tiktok.model.dto.videoDTO.EditResponseVideoDTO;
import com.tiktok.model.dto.videoDTO.VideoWithoutOwnerDTO;
import com.tiktok.model.exceptions.BadRequestException;
import com.tiktok.model.exceptions.NotFoundException;
import com.tiktok.model.exceptions.UnauthorizedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.plaf.LabelUI;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@RestController
public class VideoController extends GlobalController {

    @PostMapping("users/{userId}/uploadVideo") // todo is the URL ok?
    public VideoWithoutOwnerDTO uploadVideo(@PathVariable int userId, @RequestParam(value = "file") MultipartFile file, @RequestParam(value = "isLive") Boolean isLve,
                                            @RequestParam(value = "isPrivate") Boolean isPrivate, @RequestParam(value = "description") String description,
                                            HttpServletRequest request) {
        int uid = getUserIdFromSession(request);
        return videoService.uploadVideo(uid, file, isLve, isPrivate, description);
    }

    @PutMapping("videos/{videoId}")
    public EditResponseVideoDTO editVideo(@PathVariable int videoId, @RequestBody EditRequestVideoDTO dto) {
        return videoService.editVideo(videoId, dto);
    }

    @DeleteMapping("videos/{videoId}")
    public String deleteVideo(@PathVariable int videoId) {
        return videoService.deleteVideo(videoId);
    }


    @GetMapping("videos/{path}")
    public void showVideo(@PathVariable String path, HttpServletResponse resp) {
        File f = new File("videos" + File.separator + path);
        if (!f.exists()) {
            throw new NotFoundException("Video does not exist!");
        }
        try {
            resp.setContentType(Files.probeContentType(f.toPath()));
            Files.copy(f.toPath(), resp.getOutputStream());
        } catch (IOException e) {
            throw new BadRequestException(e.getMessage(), e);
        }
    }
    @PutMapping("videos/{videoId}/likes")
    public String likeVideo (@PathVariable int videoId, HttpServletRequest request){
        int userId = getUserIdFromSession(request);
        return videoService.likeVideo(videoId, userId);
    }

    @GetMapping("/users/{userId}/videos")
    public List<VideoWithoutOwnerDTO> showMyVideos(@PathVariable int userId, HttpServletRequest request){
        int validatedId = getUserIdFromSession(request);
        if (validatedId != userId){
            throw new UnauthorizedException("You should be logged in!");
        }
        return videoService.showMyVideos(userId);
    }

    @GetMapping("/videos/{videoId}/comments")
    public List<CommentWithoutVideoDTO> showAllComments(@PathVariable int videoId, HttpServletRequest request){
        int userId = getUserIdFromSession(request);
        return videoService.showAllComments(videoId);
    }

    @GetMapping("/videos/{videoId}/commentsOrderdByLastAdd")
    public List<CommentWithoutVideoDTO> showAllCommentsOrderByLastAdd (@PathVariable int videoId, HttpServletRequest request){
        int userId = getUserIdFromSession(request);
        return videoService.showAllCommentsOrderByLastAdd(videoId);
    }

//    @GetMapping("/videos/showByLikes")
//    public List<VideoWithoutOwnerDTO> showAllByLikes(HttpServletRequest request){
//        int userId = getUserIdFromSession(request);
//        return videoService.showAllByLikes();
//    }
}