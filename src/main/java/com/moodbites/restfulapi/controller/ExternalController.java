package com.moodbites.restfulapi.controller;

import com.moodbites.restfulapi.service.FormService;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moodbites.restfulapi.util.ErrorMessage;
import com.moodbites.restfulapi.util.HTTPCode;

import jakarta.servlet.http.HttpServletRequest;

import com.moodbites.restfulapi.service.AuthService;
import com.moodbites.restfulapi.dto.MoodFormDTO;
import com.moodbites.restfulapi.model.Session;
import com.moodbites.restfulapi.model.User;
import com.moodbites.restfulapi.model.enums.Mood;

@RestController
@CrossOrigin
@RequestMapping(value = "${storage.api-prefix}/external")
public class ExternalController {

    @Autowired
    private AuthService authService;

    @Autowired
    private FormService formService;

    private Object data = "";

    @GetMapping("/{mood}/{userId}")
    public ResponseEntity<Object> getPreferences(HttpServletRequest request, @PathVariable String mood,
            @PathVariable String userId) {
        HTTPCode httpCode = HTTPCode.OK;
        try {
            if (Mood.checkExist(mood)) {
                Mood moodEnum = Mood.fromString(mood);
                Optional<User> userOpt = authService.findUserById(userId);
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    data = formService.getPreferenceByMoodAndUser(moodEnum, user);
                } else {
                    httpCode = HTTPCode.FORBIDDEN;
                    data = new ErrorMessage(httpCode, "Authentication Failed");
                }
            } else {
                httpCode = HTTPCode.BAD_REQUEST;
                data = new ErrorMessage(httpCode, "Invalid mood value");
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
