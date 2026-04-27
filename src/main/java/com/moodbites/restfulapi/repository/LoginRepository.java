package com.moodbites.restfulapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.moodbites.restfulapi.model.Login;

import java.util.Optional;

public interface LoginRepository extends JpaRepository<Login, String> {
    public Optional<Login> findByEmail(String email);
}
