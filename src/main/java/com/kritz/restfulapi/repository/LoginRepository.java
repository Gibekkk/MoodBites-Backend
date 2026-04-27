package com.kritz.restfulapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kritz.restfulapi.model.Login;

import java.util.Optional;

public interface LoginRepository extends JpaRepository<Login, String> {
    public Optional<Login> findByEmail(String email);
}
