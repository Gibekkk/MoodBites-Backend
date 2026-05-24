package com.moodbites.restfulapi.controller;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moodbites.restfulapi.util.ErrorMessage;
import com.moodbites.restfulapi.util.HTTPCode;

import jakarta.servlet.http.HttpServletRequest;

import com.moodbites.restfulapi.service.AuthService;
import com.moodbites.restfulapi.service.NotificationService;
import com.moodbites.restfulapi.model.Notification;
import com.moodbites.restfulapi.model.Session;

@RestController
@CrossOrigin
@RequestMapping(value = "${storage.api-prefix}/notification")
public class NotificationController {

    @Autowired
    private AuthService authService;

    @Autowired
    private NotificationService notificationService;

    private Object data = "";

    @GetMapping
    public ResponseEntity<Object> getNotifications(HttpServletRequest request) {
        String sessionToken = request.getHeader("Token");
        HTTPCode httpCode = HTTPCode.OK;
        try {
            Optional<Session> sessionOpt = authService.findSessionBySessionToken(sessionToken);
            if (sessionOpt.isPresent()) {
                Session session = sessionOpt.get();
                ArrayList<Map<String, Object>> notifications = new ArrayList<>();
                for (Notification notification : notificationService
                        .getNotificationsByUser(session.getUserId())) {
                    notifications.add(Map.of(
                            "id", notification.getId(),
                            "title", notification.getTitle(),
                            "content", notification.getContent(),
                            "createdAt", notification.getCreatedAt(),
                            "seenAt", notification.getSeenAt()));
                }
                data = notifications;
            } else {
                httpCode = HTTPCode.FORBIDDEN;
                data = new ErrorMessage(httpCode, "Authentication Failed");
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

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Object> deleteNotification(HttpServletRequest request,
            @PathVariable String notificationId) {
        String sessionToken = request.getHeader("Token");
        HTTPCode httpCode = HTTPCode.OK;
        try {
            Optional<Session> sessionOpt = authService.findSessionBySessionToken(sessionToken);
            if (sessionOpt.isPresent()) {
                Session session = sessionOpt.get();
                Optional<Notification> notificationOpt = notificationService
                        .getNotificationByUserAndId(session.getUserId(), notificationId);
                if (notificationOpt.isPresent()) {
                    Notification notification = notificationOpt.get();
                    notificationService.deleteNotification(notification);
                    data = Map.of(
                            "message", "Notification Deleted Successfully");
                } else {
                    httpCode = HTTPCode.NOT_FOUND;
                    data = new ErrorMessage(httpCode, "Notification Not Found");
                }
            } else {
                httpCode = HTTPCode.FORBIDDEN;
                data = new ErrorMessage(httpCode, "Authentication Failed");
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

    @GetMapping("/{notificationId}")
    public ResponseEntity<Object> getNotificationById(HttpServletRequest request,
            @PathVariable String notificationId) {
        String sessionToken = request.getHeader("Token");
        HTTPCode httpCode = HTTPCode.OK;
        try {
            Optional<Session> sessionOpt = authService.findSessionBySessionToken(sessionToken);
            if (sessionOpt.isPresent()) {
                Session session = sessionOpt.get();
                Optional<Notification> notificationOpt = notificationService
                        .getNotificationByUserAndId(session.getUserId(), notificationId);
                if (notificationOpt.isPresent()) {
                    Notification notification = notificationOpt.get();
                    notificationService.setNotificationRead(notification);
                    data = Map.of(
                            "id", notification.getId(),
                            "title", notification.getTitle(),
                            "content", notification.getContent(),
                            "createdAt", notification.getCreatedAt(),
                            "seenAt", notification.getSeenAt());
                } else {
                    httpCode = HTTPCode.NOT_FOUND;
                    data = new ErrorMessage(httpCode, "Notification Not Found");
                }
            } else {
                httpCode = HTTPCode.FORBIDDEN;
                data = new ErrorMessage(httpCode, "Authentication Failed");
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
