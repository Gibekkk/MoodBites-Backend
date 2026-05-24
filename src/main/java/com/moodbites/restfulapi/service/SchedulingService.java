package com.moodbites.restfulapi.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

@Service
public class SchedulingService {
    @Autowired
    private OTPService otpService;

    @Autowired
    private AuthService authService;

    SchedulingService(AuthService authService) {
        this.authService = authService;
    } 
    
    @Scheduled(fixedRate = 5000)
    public void doSomethingEvery5Secs() {
        otpService.clearRedundantOTP();
        authService.deleteExpiredSessions();
    }
}