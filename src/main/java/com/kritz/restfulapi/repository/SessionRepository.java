package com.kritz.restfulapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kritz.restfulapi.model.Session;


public interface SessionRepository extends JpaRepository<Session, String> {
    public Optional<Session> findByToken(String token);
    public Optional<Session> findByFcmToken(String fcmToken);
}
