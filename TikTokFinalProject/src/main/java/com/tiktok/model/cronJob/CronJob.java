package com.tiktok.model.cronJob;

import com.tiktok.model.entities.User;
import com.tiktok.model.entities.Video;
import com.tiktok.model.repository.UserRepository;
import com.tiktok.model.repository.VideoRepository;
import com.tiktok.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class CronJob {

    @Autowired
    private VideoRepository videoRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    //Seconds
    //Minutes
    //Hours
    //Day-of-Month
    //Month
    //Day-of-Week
    //Year (optional field)

    @Scheduled(cron = "59 59 23 28 * *") // every month on 28th at 23:59:59
    public void deleteAllVideosWithoutUser() {
        List<Video> videosWithoutOwner = videoRepository.deleteAllVideosWithoutUser();
        videoRepository.deleteAll(videosWithoutOwner);
    }

    @Scheduled(cron = "59 59 11 * * MON ")//every monday at 11:59:59
    public void sendEmailsToUsersWhoForgotToLogIn() {
        List<User> inactiveUsers = userRepository.findAllUsersWhoForgotToLogIn(LocalDateTime.now().toString());
        String subject = "Tik-Tok - You have been inactive too long!";
        String content = "Dear [[name]],<br>"
                + "Please log in again! You missed new posted videos from your subscribers!<br>"
                + "Thank you!,<br>"
                + "Tik Tok Team";
        for (User user : inactiveUsers) {
            try {
                userService.sendEmail(user, subject, content);
            } catch (MessagingException | UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
    }
}