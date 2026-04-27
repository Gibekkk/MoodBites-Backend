package com.kritz.restfulapi.service;

import java.util.Optional;
import java.util.Random;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kritz.restfulapi.model.OTP;
import com.kritz.restfulapi.model.Login;
import com.kritz.restfulapi.model.enums.OTPType;
import com.kritz.restfulapi.repository.OTPRepository;
import com.kritz.restfulapi.repository.LoginRepository;

import jakarta.transaction.Transactional;

@Service
public class OTPService {

    @Autowired
    private OTPRepository otpRepository;

    @Autowired
    private LoginRepository loginRepository;

    // @Autowired
    // private CleanUpService cleanUpService;

    private int OTP_LENGTH = 6;
    private int OTP_TIME_OUT = 5;
    private int OTP_CLEAR = 30;

    @Transactional
    public void deleteOTP(OTP otp, boolean removeLogin) {
        boolean isRegistration = otp.getOtpType() == OTPType.REGISTER;
        Login login = otp.getIdLogin();

        login.setOtp(null);
        otp.setIdLogin(null);

        otpRepository.delete(otp);
        if (isRegistration && removeLogin)
            // cleanUpService.cleanLogin(login);
                loginRepository.delete(login);
    }

    // Generate dan simpan OTP
    public OTP generateOTP(Login login, OTPType otpType) {
        clearExistingOTP(login);
        String code = String.format("%0" + OTP_LENGTH + "d", new Random().nextInt(999_999));
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(OTP_TIME_OUT);
        OTP otp = new OTP();
        otp.setId(null);
        otp.setIdLogin(login);
        otp.setKode(code);
        otp.setValidUntil(expiry);
        otp.setOtpType(otpType);
        otp.setCreatedAt(LocalDateTime.now());
        otp.setEditedAt(LocalDateTime.now());
        otpRepository.save(otp);
        return otp;
    }

    public Optional<OTP> refreshOTP(Login login) {
        String code = String.format("%0" + OTP_LENGTH + "d", new Random().nextInt(999_999));
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(OTP_TIME_OUT);
        Optional<OTP> existingOtp = otpRepository.findByIdLogin(login);
        if (existingOtp.isPresent()) {
            OTP otp = existingOtp.get();
            otp.setValidUntil(expiry);
            otp.setKode(code);
            otpRepository.save(otp);
            return Optional.of(otp);
        }
        return Optional.empty();
    }

    // public Boolean deleteOTP(Login login) {
    //     Optional<OTP> existingOtp = otpRepository.findByIdLogin(login);
    //     if (existingOtp.isPresent()) {
    //         clearExistingOTP(login);
    //         return true;
    //     }
    //     return false;
    // }

    public void clearExistingOTP(Login login) {
        Optional<OTP> existingOtp = otpRepository.findByIdLogin(login);
        if (existingOtp.isPresent()) {
            OTP otp = existingOtp.get();
            deleteOTP(otp, true);
        }
    }

    @Transactional
    public void clearRedundantOTP() {
        List<OTP> otps = otpRepository.findAll();
        for (OTP otp : otps) {
            if (otp.getValidUntil().plusMinutes(OTP_CLEAR).isBefore(LocalDateTime.now())) {
                boolean isRegistration = otp.getOtpType() == OTPType.REGISTER;
                deleteOTP(otp, isRegistration);
            }
        }
    }

    @Transactional
    public Optional<Login> verifyOTP(String loginId, String code) {
        List<OTP> otps = otpRepository.findAll();
        for (OTP otp : otps) {
            if (otp.getIdLogin().getId().equals(loginId) && otp.getKode().equals(code)
                    && otp.getValidUntil().isAfter(LocalDateTime.now())) {
                Login tempLogin = otp.getIdLogin();
                deleteOTP(otp, false);
                return Optional.of(tempLogin);
            }
        }
        return Optional.empty();
    }

}
