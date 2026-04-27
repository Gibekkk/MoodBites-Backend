package com.kritz.restfulapi.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.kritz.restfulapi.dto.LoginDTO;
import com.kritz.restfulapi.dto.ProfilDTO;
import com.kritz.restfulapi.dto.RegisterDTO;
import com.kritz.restfulapi.dto.VerifyOTPDTO;
import com.kritz.restfulapi.service.EmailService;
import com.kritz.restfulapi.service.LoginService;
import com.kritz.restfulapi.service.OTPService;
import com.kritz.restfulapi.util.ErrorMessage;
import com.kritz.restfulapi.util.HTTPCode;

import com.kritz.restfulapi.model.Session;
import com.kritz.restfulapi.model.Toko;
import com.kritz.restfulapi.model.enums.Level;
import com.kritz.restfulapi.model.enums.OTPType;
import com.kritz.restfulapi.model.Login;
import com.kritz.restfulapi.model.OTP;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping(value = "${storage.api-prefix}/session")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private OTPService otpService;

    @Autowired
    private EmailService emailService;

    private Object data = "";

    LoginController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/login/toko")
    public ResponseEntity<Object> login(@RequestBody LoginDTO loginDTO) {
        HTTPCode httpCode = HTTPCode.OK;
        try {
            loginDTO.checkDTO();
            Optional<Session> sessionOpt = loginService.authenticateUser(loginDTO.getEmail(), loginDTO.getPassword(),
                    loginDTO.getFcmToken());
            if (sessionOpt.isPresent()) {
                Session session = sessionOpt.get();
                data = Map.of(
                        "loginId", session.getIdLogin().getId(),
                        "tokoId", session.getIdLogin().getIdToko().getId(),
                        "token", session.getToken());
            } else {
                httpCode = HTTPCode.UNAUTHORIZED;
                data = new ErrorMessage(httpCode, "Email atau Password Salah");
            }
        } catch (IllegalArgumentException e) {
            httpCode = HTTPCode.BAD_REQUEST;
            data = new ErrorMessage(httpCode, e.getMessage());
        } catch (Exception e) {
            httpCode = HTTPCode.INTERNAL_SERVER_ERROR;
            data = new ErrorMessage(httpCode, e.getMessage());
        }
        return ResponseEntity
                .status(httpCode.getStatus())
                .contentType(MediaType.APPLICATION_JSON)
                .body(data);
    }

    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody RegisterDTO registerDTO) {
        HTTPCode httpCode = HTTPCode.OK;
        try {
            registerDTO.checkDTO();
            if (loginService.isEmailAvailable(registerDTO.getEmail())) {
                Toko newToko = loginService.RegisterToko(registerDTO.getEmail(), registerDTO.getPassword(),
                        registerDTO.getFcmToken(), registerDTO.getNama(), registerDTO.getDeskripsi());
                OTP newOTP = otpService.generateOTP(newToko.getIdLogin(), OTPType.REGISTER);
                emailService.sendOTPRegisToLogin(newToko.getIdLogin(), newOTP);
                data = Map.of(
                        "loginId", newToko.getIdLogin().getId(),
                        "otpValidUntil", newOTP.getValidUntil());
            } else {
                httpCode = HTTPCode.CONFLICT;
                data = new ErrorMessage(httpCode, "Email sudah terdaftar");
            }
        } catch (IllegalArgumentException e) {
            httpCode = HTTPCode.BAD_REQUEST;
            data = new ErrorMessage(httpCode, e.getMessage());
        } catch (Exception e) {
            httpCode = HTTPCode.INTERNAL_SERVER_ERROR;
            data = new ErrorMessage(httpCode, e.getMessage());
        }
        return ResponseEntity
                .status(httpCode.getStatus())
                .contentType(MediaType.APPLICATION_JSON)
                .body(data);
    }

    @PostMapping("/register/verify")
    public ResponseEntity<Object> registerVerify(@RequestBody VerifyOTPDTO verifyOTPDTO) {
        HTTPCode httpCode = HTTPCode.OK;
        try {
            verifyOTPDTO.checkDTO();
            Optional<Login> loginOpt = otpService.verifyOTP(verifyOTPDTO.getLoginId(), verifyOTPDTO.getOtp());
            if (loginOpt.isPresent()) {
                Login login = loginOpt.get();
                Session session = loginService.verifyUser(login);
                data = Map.of(
                        "loginId", session.getIdLogin().getId(),
                        "tokoId", session.getIdLogin().getIdToko().getId(),
                        "token", session.getToken());
            } else {
                httpCode = HTTPCode.UNAUTHORIZED;
                data = new ErrorMessage(httpCode, "OTP Tidak Valid atau Telah Kedaluwarsa");
            }
        } catch (IllegalArgumentException e) {
            httpCode = HTTPCode.BAD_REQUEST;
            data = new ErrorMessage(httpCode, e.getMessage());
        } catch (Exception e) {
            httpCode = HTTPCode.INTERNAL_SERVER_ERROR;
            data = new ErrorMessage(httpCode, e.getMessage());
        }
        return ResponseEntity
                .status(httpCode.getStatus())
                .contentType(MediaType.APPLICATION_JSON)
                .body(data);
    }

    @PostMapping("/refreshOtp/{loginId}")
    public ResponseEntity<Object> refreshOtp(@PathVariable String loginId) {
        HTTPCode httpCode = HTTPCode.OK;
        try {
            Optional<Login> loginOpt = loginService.findLoginById(loginId);
            if (loginOpt.isPresent()) {
                Login login = loginOpt.get();
                Optional<OTP> otpOpt = otpService.refreshOTP(login);
                if(otpOpt.isPresent()) {
                    OTP otp = otpOpt.get();
                    emailService.sendOTPRegisToLogin(login, otp);
                data = Map.of(
                        "loginId", login.getId(),
                        "otpValidUntil", otp.getValidUntil());
                } else {
                    httpCode = HTTPCode.NOT_FOUND;
                    data = new ErrorMessage(httpCode, "OTP Tidak Ditemukan");
                }

            } else {
                httpCode = HTTPCode.UNAUTHORIZED;
                data = new ErrorMessage(httpCode, "Login ID Tidak Ditemukan");
            }
        } catch (IllegalArgumentException e) {
            httpCode = HTTPCode.BAD_REQUEST;
            data = new ErrorMessage(httpCode, e.getMessage());
        } catch (Exception e) {
            httpCode = HTTPCode.INTERNAL_SERVER_ERROR;
            data = new ErrorMessage(httpCode, e.getMessage());
        }
        return ResponseEntity
                .status(httpCode.getStatus())
                .contentType(MediaType.APPLICATION_JSON)
                .body(data);
    }

    @PatchMapping("/profile/toko")
    public ResponseEntity<Object> updateProfile(HttpServletRequest request, @RequestBody ProfilDTO profilDTO) {
        String sessionToken = request.getHeader("Token");
        HTTPCode httpCode = HTTPCode.OK;
        try {
            profilDTO.checkDTO();
            Optional<Session> sessionOpt = loginService.findSessionBySessionToken(sessionToken);
            if (sessionOpt.isPresent()) {
                Session session = sessionOpt.get();
                if (session.getIdLogin().getLevel() == Level.TOKO) {
                    Toko toko = session.getIdLogin().getIdToko();
                    toko = loginService.editProfilToko(toko, profilDTO.getNama(), profilDTO.getDeskripsi());
                    data = Map.of(
                            "idToko", toko.getId(),
                            "nama", toko.getNama(),
                            "deskripsi", Optional.ofNullable(toko.getDeskripsi()).orElse(""));
                } else {
                    httpCode = HTTPCode.FORBIDDEN;
                    data = new ErrorMessage(httpCode, "Akses Ditolak");
                }
            } else {
                httpCode = HTTPCode.BAD_REQUEST;
                data = new ErrorMessage(httpCode, "Pemeriksaan Autentikasi Gagal");
            }
        } catch (IllegalArgumentException e) {
            httpCode = HTTPCode.BAD_REQUEST;
            data = new ErrorMessage(httpCode, e.getMessage());
        } catch (Exception e) {
            httpCode = HTTPCode.INTERNAL_SERVER_ERROR;
            data = new ErrorMessage(httpCode, e.getMessage());
        }

        return ResponseEntity
                .status(httpCode.getStatus())
                .contentType(MediaType.APPLICATION_JSON)
                .body(data);
    }
}
