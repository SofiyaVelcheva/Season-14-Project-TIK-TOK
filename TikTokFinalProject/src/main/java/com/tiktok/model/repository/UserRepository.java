package com.tiktok.model.repository;

import com.tiktok.model.entities.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUsernameAndPassword(String username, String pass);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<User> findByPhoneNumber(String phoneNumber);

    Optional<User> findUserByVerificationCode(String verificationCode);


    @Query(value = "SELECT u.id FROM users AS u WHERE u.verification_code = ':verificationCode';", nativeQuery = true)
    Optional<User> findByVerificationCode(@Param("verificationCode") String verificationCode);

    @Query(value = "SELECT * FROM users as u " +
            "LEFT JOIN subscribers as s ON(u.id = s.publisher_id) " +
            "WHERE u.username LIKE :username AND u.username NOT LIKE '%delete%'" +
            "GROUP BY u.id " +
            "ORDER BY COUNT(s.publisher_id) DESC", nativeQuery = true)
    List<User> findAllByUsername(@Param("username") String username, Pageable pageable);


    @Query(value = "SELECT * FROM users as u LEFT JOIN subscribers as s ON u.id = s.publisher_id " +
            "WHERE subscriber_id = :uid AND username NOT LIKE '%delete%'", nativeQuery = true)
    List<User> getAllSub(@Param("uid") int uid, Pageable pageable);

    @Query(value = "SELECT * FROM users AS u WHERE u.last_login <= DATE_SUB(:date,INTERVAL 7 DAYS)  ", nativeQuery = true)
    List<User> findAllUsersWhoForgotToLogIn(@Param("date") String localDateNow);
}
