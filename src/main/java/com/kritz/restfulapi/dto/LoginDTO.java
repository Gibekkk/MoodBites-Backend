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
public class LoginDTO {
    private String email;
    private String password;
    private String fcmToken;

    public void checkDTO() {
        trim();
        checkLength();
        if (this.email == null)
            throw new IllegalArgumentException("Email Tidak Boleh Bernilai NULL");
        if (this.password == null)
            throw new IllegalArgumentException("Password Tidak Boleh Bernilai NULL");
        if (this.fcmToken == null)
            throw new IllegalArgumentException("Token FCM Tidak Boleh Bernilai NULL");
    }

    public void checkLength() {
        boolean email = Optional.ofNullable(this.email)
                .map(s -> s.length() <= 50 && s.matches("^[a-zA-Z0-9. _%+-]+@[a-zA-Z0-9. -]+\\.[a-zA-Z]{2,}$"))
                .orElse(true);

        if (!email)
            throw new IllegalArgumentException("Email Tidak Valid atau Melewati Batas Karakter");
    }

    public void trim() {
        this.email = Optional.ofNullable(this.email).map(String::trim).filter(s -> !s.isBlank()).orElse(null);
        this.password = Optional.ofNullable(this.password).map(String::trim).filter(s -> !s.isBlank()).orElse(null);
        this.fcmToken = Optional.ofNullable(this.fcmToken).map(String::trim).filter(s -> !s.isBlank()).orElse(null);
    }

}
