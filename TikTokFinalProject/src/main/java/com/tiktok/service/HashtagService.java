package com.tiktok.service;

import com.tiktok.model.entities.Hashtag;
import com.tiktok.model.entities.Video;
import com.tiktok.model.exceptions.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class HashtagService extends GlobalService {
    private static final String VALID_HASHTAG_REGEX = "#[A-Za-z0-9_]+";
    private static final int HASHTAG_LENGTH = 50;

    public void addHashtags(Video video) {
        if (video.getDescription() != null) {
            String[] allWords = video.getDescription().split("\\s+");
                     for (String hashtag : allWords) {
                Pattern p = Pattern.compile(VALID_HASHTAG_REGEX);
                Matcher m = p.matcher(hashtag);
                if (m.find()) {
                    addHashtag(video, hashtag);
                }
            }
        }
    }

    private void addHashtag(Video video, String hashtag) {
        if (hashtag.length() > HASHTAG_LENGTH) {
            throw new BadRequestException("Hashtag length should be maximum 50 symbols.");
        }
        Optional<Hashtag> optionalHashtag = hashtagRepository.findHashtagByText(hashtag);
        Hashtag hash;
        if (optionalHashtag.isPresent()) {
            hash = optionalHashtag.get();
        } else {
            hash = new Hashtag();
            hash.setText(hashtag);
            hashtagRepository.save(hash);
        }
        if (video.getHashtags() == null) {
            video.setHashtags(new ArrayList<>());
        }
        video.getHashtags().add(hash);
    }
}
