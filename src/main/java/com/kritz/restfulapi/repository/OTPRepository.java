package com.kritz.restfulapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

import com.kritz.restfulapi.model.OTP;
import com.kritz.restfulapi.model.Login;

public interface OTPRepository extends JpaRepository<OTP, String> {
    Optional<OTP> findByIdLogin(Login IdLogin);
}
