package com.tiktok.model.repository;

import com.tiktok.model.entities.Sound;
import com.tiktok.model.entities.Video;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SoundRepository extends JpaRepository <Sound, Long> {


}
