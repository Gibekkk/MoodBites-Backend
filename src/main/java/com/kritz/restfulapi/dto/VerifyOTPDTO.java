package com.kritz.restfulapi.dto;

import lombok.Setter;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VerifyOTPDTO {
    private String loginId;
    private String otp;


    public void checkDTO() {
        trim();
        if(this.loginId == null) throw new IllegalArgumentException("Login ID Tidak Boleh Bernilai NULL");
        if(this.otp == null) throw new IllegalArgumentException("OTP Tidak Boleh Bernilai NULL");
    }

    public void trim() {
        this.loginId = Optional.ofNullable(this.loginId).map(String::trim).filter(s -> !s.isBlank()).orElse(null);
        this.otp = Optional.ofNullable(this.otp).map(String::trim).filter(s -> !s.isBlank()).orElse(null);
    }

}

