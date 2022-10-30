package com.tiktok.model.repository;

import com.tiktok.model.entities.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {

    @Query(value = "SELECT * FROM messages WHERE sender_id IN(:receiver,:sender) " +
            "AND receiver_id IN(:receiver,:sender) " +
            "ORDER BY send_at desc ", nativeQuery = true)
    List<Message> correspondence(@Param("receiver") int rid, @Param("sender") int sid, Pageable pageable);
}
