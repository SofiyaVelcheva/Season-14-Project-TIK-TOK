package com.tiktok.controller;

import com.tiktok.model.dto.videoDTO.request.VideoRequestEditDTO;
import com.tiktok.model.dto.videoDTO.response.EditResponseVideoDTO;
import com.tiktok.model.dto.videoDTO.response.VideoResponseDTO;
import com.tiktok.model.dto.videoDTO.response.VideoResponseMessageDTO;
import com.tiktok.model.dto.videoDTO.response.VideoResponseWithoutOwnerDTO;
import com.tiktok.model.exceptions.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class VideoController extends GlobalController {

    @PostMapping("/users/{userId}/uploadVideo")
    public ResponseEntity<VideoResponseDTO> uploadVideo(@PathVariable int userId,
                                                        @RequestParam(value = "file") MultipartFile file,
                                                        @RequestParam(value = "isLive") Boolean isLve,
                                                        @RequestParam(value = "isPrivate") Boolean isPrivate,
                                                        @RequestParam(value = "description") String description,
                                                        HttpServletRequest request) {
        int userFromSess = getUserIdFromSession(request);
        if (userFromSess != userId){
            throw new UnauthorizedException("You have to log in from your account!");
        }
        return new ResponseEntity<>(videoService.uploadVideo(userId, file, isLve, isPrivate, description), HttpStatus.CREATED);
    }

    @PutMapping("/videos/{videoId}")
    public ResponseEntity<EditResponseVideoDTO> editVideo(@PathVariable int videoId,
                                                          @RequestBody VideoRequestEditDTO dto,
                                                          HttpServletRequest request) {
        int userId = getUserIdFromSession(request);
        return new ResponseEntity<>(videoService.editVideo(videoId, dto, userId), HttpStatus.OK);
    }

    @DeleteMapping("/videos/{videoId}")
    public ResponseEntity<VideoResponseMessageDTO> deleteVideo(@PathVariable int videoId,
                                                               HttpServletRequest request) {
        int userId = getUserIdFromSession(request);
        return new ResponseEntity<>(videoService.deleteVideo(videoId, userId), HttpStatus.OK);
    }

    @PutMapping("/videos/{videoId}/like")
    public ResponseEntity<VideoResponseMessageDTO> likeVideo(@PathVariable int videoId,
                                                             HttpServletRequest request) {
        int userId = getUserIdFromSession(request);
        return new ResponseEntity<>(videoService.likeVideo(videoId, userId), HttpStatus.OK);
    }

    @PostMapping("/users/myVideos")
    public ResponseEntity<List<VideoResponseWithoutOwnerDTO>> showMyVideos(@RequestParam(defaultValue = "0") int pageNumber,
                                                                           @RequestParam(defaultValue = "3") int videosPerPage,
                                                                           HttpServletRequest request) {
        int userId = getUserIdFromSession(request);
        return new ResponseEntity<>(videoService.showMyVideos(userId, pageNumber, videosPerPage), HttpStatus.OK);
    }

}