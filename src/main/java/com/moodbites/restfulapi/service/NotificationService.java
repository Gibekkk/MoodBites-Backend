package com.moodbites.restfulapi.service;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.moodbites.restfulapi.model.Notification;
import com.moodbites.restfulapi.model.User;
import com.moodbites.restfulapi.repository.NotificationRepository;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private FirebaseMessagingService firebaseMessagingService;

    public ArrayList<Notification> getNotificationsByUser(User user) {
        return notificationRepository.findByDeletedAtIsNullAndUserId(user);
    }

    // public Object getNotificationDetailsByUser(String idUser) {
    //     ArrayList<Notification> listNotification = getNotificationsByUser(idUser);
    //     ArrayList<Object> result = new ArrayList<Object>();
    //     if (listNotification.size() > 0) {
    //         for (Notification notifikasi : listNotification) {
    //             Agenda agenda = notifikasi.getIdAgenda();
    //             Reminder reminder = notifikasi.getIdReminder();
    //             result.add(Map.of(
    //                     "id", notifikasi.getId(),
    //                     "message", notifikasi.getDeskripsiNotification(),
    //                     "dateCreated", notifikasi.getCreatedAt(),
    //                     "title", notifikasi.getNamaNotification(),
    //                     "isClicked", notifikasi.getIsRead(),
    //                     "jenisNotification", notifikasi.getJenisNotification().toString(),
    //                     "agenda", agenda == null ? ""
    //                             : Map.of(
    //                                     "id", agenda.getId(),
    //                                     "contactName", agenda.getIdKontak().getNama(),
    //                                     "title", agenda.getNamaAgenda(),
    //                                     "time", agenda.getJadwalAgenda(),
    //                                     "isTriggered", agenda.getIsNotified(),
    //                                     "status", agenda.getStatusAgenda().toString(),
    //                                     "appointmentPlace", agenda.getLokasiAgenda()),
    //                     "reminder", reminder == null ? ""
    //                             : Map.of(
    //                                     "id", reminder.getId(),
    //                                     "contactName", reminder.getIdKontak().getNama(),
    //                                     "notes", reminder.getCatatanReminder(),
    //                                     "time", reminder.getJadwalReminder(),
    //                                     "isTriggered", reminder.getIsNotified(),
    //                                     "frequency", reminder.getPerulangan().toString())));
    //         }
    //     }
    //     return result;
    // }

    // public Optional<Notification> deleteNotificationsByUser(String idUser, String notifikasiId) {
    //     User user = userService.findUserById(idUser).get();
    //     Optional<Notification> optionalNotification = getNotificationById(notifikasiId);
    //     if (optionalNotification.isPresent()) {
    //         Notification notif = optionalNotification.get();
    //         Boolean pass = false;
    //         if (!notif.getIsDeleted()) {
    //             if (notif.getJenisNotification().equals(JenisNotification.AGENDA)) {
    //                 if (!notif.getIdAgenda().getIsDeleted())
    //                     pass = true;
    //             } else if (notif.getJenisNotification().equals(JenisNotification.REMINDER)) {
    //                 if (!notif.getIdReminder().getIsDeleted())
    //                     pass = true;
    //             } else {
    //                 pass = true;
    //             }
    //         }
    //         if (pass) {
    //             if (notif.getIdUser().equals(user)) {
    //                 notif.setIsDeleted(true);
    //                 notif.setDeletedAt(LocalDate.now());
    //                 return Optional.of(notificationRepository.save(notif));
    //             }
    //         }
    //     }
    //     return Optional.empty();
    // }

    // public ArrayList<String> deleteNotificationListByUser(String idUser, List<String> notifikasiIdList) {
    //     ArrayList<String> deletedNotification = new ArrayList<String>();
    //     for (String idNotification : notifikasiIdList) {
    //         if (deleteNotificationsByUser(idUser, idNotification).isPresent())
    //             deletedNotification.add(idNotification);
    //     }
    //     return deletedNotification;
    // }

    // public Optional<Notification> readNotificationsByUser(String idUser, String notifikasiId) {
    //     User user = userService.findUserById(idUser).get();
    //     Optional<Notification> optionalNotification = getNotificationById(notifikasiId);
    //     if (optionalNotification.isPresent()) {
    //         Notification notif = optionalNotification.get();
    //         Boolean pass = false;
    //         if (!notif.getIsDeleted()) {
    //             if (notif.getJenisNotification().equals(JenisNotification.AGENDA)) {
    //                 if (!notif.getIdAgenda().getIsDeleted())
    //                     pass = true;
    //             } else if (notif.getJenisNotification().equals(JenisNotification.REMINDER)) {
    //                 if (!notif.getIdReminder().getIsDeleted())
    //                     pass = true;
    //             } else {
    //                 pass = true;
    //             }
    //         }
    //         if (pass) {
    //             if (notif.getIdUser().equals(user)) {
    //                 notif.setIsRead(true);
    //                 return Optional.of(notificationRepository.save(notif));
    //             }
    //         }
    //     }
    //     return Optional.empty();
    // }

    public ArrayList<Notification> getAllNotification() {
        return notificationRepository.findByDeletedAtIsNull();
    }

    public Optional<Notification> getNotificationByUserAndId(User user, String idNotification) {
        return notificationRepository.findByDeletedAtIsNullAndUserIdAndId(user, idNotification);
    }
}
