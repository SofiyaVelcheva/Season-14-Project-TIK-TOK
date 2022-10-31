package com.tiktok.model.repository;

import com.tiktok.model.entities.Sound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SoundRepository extends JpaRepository <Sound, Integer> {


}
