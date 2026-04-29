package com.moodbites.restfulapi.service;

import java.util.Optional;
import java.util.Random;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.moodbites.restfulapi.model.OTP;
import com.moodbites.restfulapi.model.User;
import com.moodbites.restfulapi.repository.OTPRepository;
import com.moodbites.restfulapi.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class OTPService {

    @Autowired
    private OTPRepository otpRepository;

    @Autowired
    private UserRepository userRepository;

    // @Autowired
    // private CleanUpService cleanUpService;

    private int OTP_LENGTH = 6;
    private int OTP_TIME_OUT = 5;
    private int OTP_CLEAR = 30;

    @Transactional
    public void deleteOTP(OTP otp) {
        User user = otp.getUserId();

        user.setOtp(null);
        otp.setUserId(null);

        otpRepository.delete(otp);
        if (user.getVerifiedAt() == null)
            // cleanUpService.cleanLogin(user);
                userRepository.delete(user);
    }

    // Generate dan simpan OTP
    public OTP generateOTP(User user) {
        clearExistingOTP(user);
        String code = String.format("%0" + OTP_LENGTH + "d", new Random().nextInt(999_999));
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(OTP_TIME_OUT);
        OTP otp = new OTP();
        otp.setId(null);
        otp.setUserId(user);
        otp.setCode(code);
        otp.setValidUntil(expiry);
        otp.setCreatedAt(LocalDateTime.now());
        otp.setEditedAt(LocalDateTime.now());
        otpRepository.save(otp);
        return otp;
    }

    public Optional<OTP> refreshOTP(User user) {
        String code = String.format("%0" + OTP_LENGTH + "d", new Random().nextInt(999_999));
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(OTP_TIME_OUT);
        Optional<OTP> existingOtp = otpRepository.findByUserId(user);
        if (existingOtp.isPresent()) {
            OTP otp = existingOtp.get();
            otp.setValidUntil(expiry);
            otp.setCode(code);
            otpRepository.save(otp);
            return Optional.of(otp);
        }
        return Optional.empty();
    }

    // public Boolean deleteOTP(Login user) {
    //     Optional<OTP> existingOtp = otpRepository.findByUserId(user);
    //     if (existingOtp.isPresent()) {
    //         clearExistingOTP(user);
    //         return true;
    //     }
    //     return false;
    // }

    public void clearExistingOTP(User user) {
        Optional<OTP> existingOtp = otpRepository.findByUserId(user);
        if (existingOtp.isPresent()) {
            OTP otp = existingOtp.get();
            deleteOTP(otp);
        }
    }

    @Transactional
    public void clearRedundantOTP() {
        List<OTP> otps = otpRepository.findAll();
        for (OTP otp : otps) {
            if (otp.getValidUntil().plusMinutes(OTP_CLEAR).isBefore(LocalDateTime.now())) {
                deleteOTP(otp);
            }
        }
    }

    @Transactional
    public Optional<User> verifyOTP(String userId, String code) {
        List<OTP> otps = otpRepository.findAll();
        for (OTP otp : otps) {
            if (otp.getUserId().getId().equals(userId) && otp.getCode().equals(code)
                    && otp.getValidUntil().isAfter(LocalDateTime.now())) {
                User tempLogin = otp.getUserId();
                deleteOTP(otp);
                return Optional.of(tempLogin);
            }
        }
        return Optional.empty();
    }

}
