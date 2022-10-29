package com.tiktok.controller;

import com.tiktok.model.dto.TextResponseDTO;
import com.tiktok.model.dto.videoDTO.request.VideoRequestEditDTO;
import com.tiktok.model.dto.videoDTO.response.*;
import com.tiktok.model.dto.videoDTO.response.EditResponseVideoDTO;
import com.tiktok.model.dto.videoDTO.response.VideoResponseDTO;
import com.tiktok.model.dto.videoDTO.response.VideoResponseWithoutOwnerDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class VideoController extends GlobalController {

    @PostMapping("/users/uploadVideo")
    public ResponseEntity<VideoResponseDTO> uploadVideo(@RequestParam(value = "file") MultipartFile file,
                                                        @RequestParam(value = "isLive") Boolean isLve,
                                                        @RequestParam(value = "isPrivate") Boolean isPrivate,
                                                        @RequestParam(value = "description") String description,
                                                        HttpServletRequest request) {
        return new ResponseEntity<>(videoService.uploadVideo(getUserIdFromSession(request), file, isLve, isPrivate, description), HttpStatus.CREATED);
    }

    @PutMapping("/videos/{videoId}")
    public ResponseEntity<EditResponseVideoDTO> editVideo(@PathVariable int videoId,
                                                          @RequestBody VideoRequestEditDTO dto,
                                                          HttpServletRequest request) {
        return new ResponseEntity<>(videoService.editVideo(videoId, dto, getUserIdFromSession(request)), HttpStatus.OK);
    }

    @PutMapping("/videos/{videoId}/like")
    public ResponseEntity<TextResponseDTO> likeVideo(@PathVariable int videoId,
                                                     HttpServletRequest request) {
        return new ResponseEntity<>(videoService.likeVideo(videoId, getUserIdFromSession(request)), HttpStatus.OK);
    }

    @DeleteMapping("/videos/{videoId}")
    public ResponseEntity<TextResponseDTO> deleteVideo(@PathVariable int videoId,
                                                       HttpServletRequest request) {
        return new ResponseEntity<>(videoService.deleteVideo(videoId, getUserIdFromSession(request)), HttpStatus.OK);
    }


    @PostMapping("/users/myVideos")
    public ResponseEntity<List<VideoResponseWithoutOwnerDTO>> showMyVideos(@RequestParam(defaultValue = "0") int pageNumber,
                                                                           @RequestParam(defaultValue = "3") int videosPerPage,
                                                                           HttpServletRequest request) {
        return new ResponseEntity<>(videoService.showMyVideos(getUserIdFromSession(request), pageNumber, videosPerPage), HttpStatus.OK);
    }

    @PostMapping("/videos/live")
    public ResponseEntity<List<VideoResponseWithoutOwnerDTO>> showLiveVideos(@RequestParam(defaultValue = "0") int pageNumber,
                                                                             @RequestParam(defaultValue = "3") int videosPerPage) {
        return new ResponseEntity<>(videoService.showLiveVideos(pageNumber, videosPerPage), HttpStatus.OK);
    }

    @GetMapping("/videos/sub")
    public ResponseEntity<List<VideoResponseUploadDTO>> getVideosPublishers(@RequestParam(value = "page", defaultValue = "0") int page,
                                                                            @RequestParam(value = "perPage", defaultValue = "10") int perPage,
                                                                            HttpServletRequest req) {
        return new ResponseEntity<>(videoService.getVideosPublishers(getUserIdFromSession(req), page, perPage), HttpStatus.OK);
    }

    @GetMapping("/videos/hashtag")
    public ResponseEntity<List<VideoResponseUploadDTO>> getAllVideosHashtag(@RequestParam(value = "text") String text,
                                                                            @RequestParam(value = "page", defaultValue = "0") int page,
                                                                            @RequestParam(value = "perPage", defaultValue = "10") int perPage) {
        return new ResponseEntity<>(videoService.getAllVideosHashtag(text, page, perPage), HttpStatus.OK);
    }
}