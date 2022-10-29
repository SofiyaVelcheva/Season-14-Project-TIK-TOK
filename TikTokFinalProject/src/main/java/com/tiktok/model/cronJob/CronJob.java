package com.tiktok.model.cronJob;

import com.tiktok.model.entities.Video;
import com.tiktok.model.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CronJob {

    @Autowired
    private VideoRepository videoRepository;

    @Scheduled(cron = "59 23 28 * * ") // every month on 28th at 23:59
    public void deleteAllVideosWithoutUser(){
        List<Video> videosWithoutOwner = videoRepository.deleteAllVideosWithoutUser();
        videoRepository.deleteAll(videosWithoutOwner);
    }
}
