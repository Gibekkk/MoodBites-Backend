// package com.kritz.restfulapi.service;

// import java.util.ArrayList;
// import java.util.List;
// import java.util.Map;
// import java.util.Optional;
// import java.time.LocalDateTime;
// import java.time.LocalDate;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;

// import com.kritz.restfulapi.dto.BroadcastDTO;
// import com.kritz.restfulapi.dto.NotifikasiDTO;
// import com.kritz.restfulapi.dto.NotificationMessage;
// import com.kritz.restfulapi.model.Reminder;
// import com.kritz.restfulapi.model.SuperAdmin;
// import com.kritz.restfulapi.model.User;
// import com.kritz.restfulapi.model.Agenda;
// import com.kritz.restfulapi.model.Broadcast;
// import com.kritz.restfulapi.model.FilterBroadcast;
// import com.kritz.restfulapi.model.JenisNotifikasi;
// import com.kritz.restfulapi.model.Notifikasi;
// import com.kritz.restfulapi.repository.NotifikasiRepository;
// import com.kritz.restfulapi.repository.BroadcastRepository;

// @Service
// public class NotifikasiService {
//     @Autowired
//     private NotifikasiRepository notifikasiRepository;

//     @Autowired
//     private BroadcastRepository broadcastRepository;

//     @Autowired
//     private UserService userService;

//     @Autowired
//     private FirebaseMessagingService firebaseMessagingService;

//     @Autowired
//     private LoginService loginService;

//     public ArrayList<Notifikasi> getNotifikasisByUser(String idUser) {
//         List<Notifikasi> listNotifikasi = getAllNotifikasi();
//         ArrayList<Notifikasi> notifikasiUser = new ArrayList<Notifikasi>();
//         for (Notifikasi notifikasi : listNotifikasi) {
//             if (notifikasi.getIdUser().getId().equals(idUser)) {
//                 notifikasiUser.add(notifikasi);
//             }
//         }
//         return notifikasiUser;
//     }

//     public void addNotifikasiReminder(NotifikasiDTO notifikasiDTO, Reminder reminder) {
//         Notifikasi notifikasi = new Notifikasi(
//                 null, // id
//                 notifikasiDTO.getNama(),
//                 notifikasiDTO.getDeskripsi(),
//                 LocalDateTime.now(),
//                 false,
//                 null,
//                 false,
//                 JenisNotifikasi.REMINDER,
//                 reminder,
//                 null,
//                 notifikasiDTO.getUser(),
//                 null);
//         notifikasiRepository.save(notifikasi);
//         String fcmToken = loginService.getFcmTokenByUser(notifikasiDTO.getUser().getId());
//         if (fcmToken != null) {
//             firebaseMessagingService.sendNotificationByToken(new NotificationMessage(
//                     fcmToken,
//                     notifikasiDTO.getNama(),
//                     notifikasiDTO.getDeskripsi(),
//                     "reminder",
//                     null,
//                     Map.of("reminderId", reminder.getId())));
//         }
//     }

//     public void addNotifikasiAgenda(NotifikasiDTO notifikasiDTO, Agenda agenda) {
//         Notifikasi notifikasi = new Notifikasi(
//                 null, // id
//                 notifikasiDTO.getNama(),
//                 notifikasiDTO.getDeskripsi(),
//                 LocalDateTime.now(),
//                 false,
//                 null,
//                 false,
//                 JenisNotifikasi.AGENDA,
//                 null,
//                 agenda,
//                 notifikasiDTO.getUser(),
//                 null);
//         notifikasiRepository.save(notifikasi);
//         String fcmToken = loginService.getFcmTokenByUser(notifikasiDTO.getUser().getId());
//         if (fcmToken != null) {
//             firebaseMessagingService.sendNotificationByToken(new NotificationMessage(
//                     fcmToken,
//                     notifikasiDTO.getNama(),
//                     notifikasiDTO.getDeskripsi(),
//                     "agenda",
//                     null,
//                     Map.of("agendaId", agenda.getId())));
//         }
//     }

//     public void addNotifikasiPesan(NotifikasiDTO notifikasiDTO) {
//         Notifikasi notifikasi = new Notifikasi(
//                 null, // id
//                 notifikasiDTO.getNama(),
//                 notifikasiDTO.getDeskripsi(),
//                 LocalDateTime.now(),
//                 false,
//                 null,
//                 false,
//                 JenisNotifikasi.PESAN,
//                 null,
//                 null,
//                 notifikasiDTO.getUser(),
//                 notifikasiDTO.getBroadcast());
//         notifikasiRepository.save(notifikasi);
//         String fcmToken = loginService.getFcmTokenByUser(notifikasiDTO.getUser().getId());
//         if (fcmToken != null) {
//             firebaseMessagingService.sendNotificationByToken(new NotificationMessage(
//                     fcmToken,
//                     notifikasiDTO.getNama(),
//                     notifikasiDTO.getDeskripsi(),
//                     "message",
//                     null,
//                     Map.of("broadcastId", notifikasiDTO.getBroadcast().getId())));
//         }
//     }

//     public int sendBroadcast(String idBroadcast) {
//         Optional<Broadcast> broadcastOpt = getBroadcastById(idBroadcast);
//         if (broadcastOpt.isPresent()) {
//             Broadcast broadcast = broadcastOpt.get();
//             if (!broadcast.isSent()) {
//                 ArrayList<User> filteredUser = userService.filterUser(broadcast.getFilter(),
//                         broadcast.getDays(), broadcast.getType());
//                 for (User user : filteredUser) {
//                     NotifikasiDTO notifikasiDTO = new NotifikasiDTO();
//                     notifikasiDTO.setNama(broadcast.getNama());
//                     notifikasiDTO.setDeskripsi(broadcast.getDeskripsi());
//                     notifikasiDTO.setUser(user);
//                     notifikasiDTO.setBroadcast(broadcast);
//                     addNotifikasiPesan(notifikasiDTO);
//                 }
//                 broadcast.setEditedAt(LocalDateTime.now());
//                 broadcastRepository.save(broadcast);
//                 return filteredUser.size();
//             }
//             return -1;
//         }
//         return -2;
//     }

//     public int filterUserBroadcast(String idBroadcast) {
//         Optional<Broadcast> broadcastOpt = getBroadcastById(idBroadcast);
//         if (broadcastOpt.isPresent()) {
//             Broadcast broadcast = broadcastOpt.get();
//             if (!broadcast.isSent()) {
//                 ArrayList<User> filteredUser = userService.filterUser(broadcast.getFilter(),
//                         broadcast.getDays(), broadcast.getType());
//                 return filteredUser.size();
//             }
//             return -1;
//         }
//         return -2;
//     }

//     public Broadcast createBroadcast(BroadcastDTO broadcastDTO) {
//         Broadcast broadcast = new Broadcast();
//         broadcast.setNama(broadcastDTO.getTitle());
//         broadcast.setDeskripsi(broadcastDTO.getContent());
//         broadcast.setFilter(
//                 Optional.ofNullable(broadcastDTO.getFilter()).map(f -> FilterBroadcast.fromString(f)).orElse(null));
//         broadcast.setDays(broadcastDTO.getDays());
//         broadcast.setType(
//                 Optional.ofNullable(broadcastDTO.getType()).map(t -> FilterBroadcast.fromString(t)).orElse(null));
//         broadcast.setEditedAt(LocalDateTime.now());
//         broadcast.setCreatedAt(LocalDateTime.now());
//         broadcast.setIdSuperAdmin(broadcastDTO.getSuperAdmin());
//         broadcast.setIsDeleted(false);
//         return broadcastRepository.save(broadcast);
//     }

//     public Optional<Broadcast> getBroadcastById(String id) {
//         Optional<Broadcast> optionalBroadcast = broadcastRepository.findById(id);
//         if (optionalBroadcast.isPresent()) {
//             Broadcast broadcast = optionalBroadcast.get();
//             if (!broadcast.getIsDeleted())
//                 return optionalBroadcast;
//         }
//         return Optional.empty();
//     }

//     public Optional<Broadcast> editBroadcast(BroadcastDTO broadcastDTO, String idBroadcast) {
//         Optional<Broadcast> broadcastOpt = getBroadcastById(idBroadcast);
//         if (broadcastOpt.isPresent()) {
//             Broadcast broadcast = broadcastOpt.get();
//             if (!broadcast.isSent()) {
//                 broadcast.setNama(broadcastDTO.getTitle());
//                 broadcast.setDeskripsi(broadcastDTO.getContent());
//                 broadcast.setFilter(Optional.ofNullable(broadcastDTO.getFilter())
//                         .map(f -> FilterBroadcast.fromString(f)).orElse(null));
//                 broadcast.setDays(broadcastDTO.getDays());
//                 broadcast.setType(Optional.ofNullable(broadcastDTO.getType()).map(t -> FilterBroadcast.fromString(t))
//                         .orElse(null));
//                 broadcast.setEditedAt(LocalDateTime.now());
//                 return Optional.of(broadcastRepository.save(broadcast));
//             }
//         }
//         return Optional.empty();
//     }

//     public Optional<Broadcast> deleteBroadcast(String idBroadcast) {
//         Optional<Broadcast> broadcastOpt = getBroadcastById(idBroadcast);
//         if (broadcastOpt.isPresent()) {
//             Broadcast broadcast = broadcastOpt.get();
//             if (!broadcast.isSent()) {
//                 broadcast.setEditedAt(LocalDateTime.now());
//                 broadcast.setDeletedAt(LocalDate.now());
//                 broadcast.setIsDeleted(true);
//                 return Optional.of(broadcastRepository.save(broadcast));
//             }
//         }
//         return Optional.empty();
//     }

//     public ArrayList<Broadcast> getAllBroadcast() {
//         ArrayList<Broadcast> result = new ArrayList<Broadcast>();
//         for (Broadcast broadcast : broadcastRepository.findAll()) {
//             if (!broadcast.getIsDeleted())
//                 result.add(broadcast);
//         }
//         return result;
//     }

//     public ArrayList<Broadcast> getBroadcastBySuperAdmin(SuperAdmin superAdmin) {
//         ArrayList<Broadcast> result = new ArrayList<Broadcast>();
//         for (Broadcast broadcast : getAllBroadcast()) {
//             if (broadcast.getIdSuperAdmin().equals(superAdmin))
//                 if (broadcast.getFilter() != null)
//                     result.add(broadcast);
//         }
//         return result;
//     }

//     public Object getNotifikasiDetailsByUser(String idUser) {
//         ArrayList<Notifikasi> listNotifikasi = getNotifikasisByUser(idUser);
//         ArrayList<Object> result = new ArrayList<Object>();
//         if (listNotifikasi.size() > 0) {
//             for (Notifikasi notifikasi : listNotifikasi) {
//                 Agenda agenda = notifikasi.getIdAgenda();
//                 Reminder reminder = notifikasi.getIdReminder();
//                 result.add(Map.of(
//                         "id", notifikasi.getId(),
//                         "message", notifikasi.getDeskripsiNotifikasi(),
//                         "dateCreated", notifikasi.getCreatedAt(),
//                         "title", notifikasi.getNamaNotifikasi(),
//                         "isClicked", notifikasi.getIsRead(),
//                         "jenisNotifikasi", notifikasi.getJenisNotifikasi().toString(),
//                         "agenda", agenda == null ? ""
//                                 : Map.of(
//                                         "id", agenda.getId(),
//                                         "contactName", agenda.getIdKontak().getNama(),
//                                         "title", agenda.getNamaAgenda(),
//                                         "time", agenda.getJadwalAgenda(),
//                                         "isTriggered", agenda.getIsNotified(),
//                                         "status", agenda.getStatusAgenda().toString(),
//                                         "appointmentPlace", agenda.getLokasiAgenda()),
//                         "reminder", reminder == null ? ""
//                                 : Map.of(
//                                         "id", reminder.getId(),
//                                         "contactName", reminder.getIdKontak().getNama(),
//                                         "notes", reminder.getCatatanReminder(),
//                                         "time", reminder.getJadwalReminder(),
//                                         "isTriggered", reminder.getIsNotified(),
//                                         "frequency", reminder.getPerulangan().toString())));
//             }
//         }
//         return result;
//     }

//     public Optional<Notifikasi> deleteNotifikasisByUser(String idUser, String notifikasiId) {
//         User user = userService.findUserById(idUser).get();
//         Optional<Notifikasi> optionalNotifikasi = getNotifikasiById(notifikasiId);
//         if (optionalNotifikasi.isPresent()) {
//             Notifikasi notif = optionalNotifikasi.get();
//             Boolean pass = false;
//             if (!notif.getIsDeleted()) {
//                 if (notif.getJenisNotifikasi().equals(JenisNotifikasi.AGENDA)) {
//                     if (!notif.getIdAgenda().getIsDeleted())
//                         pass = true;
//                 } else if (notif.getJenisNotifikasi().equals(JenisNotifikasi.REMINDER)) {
//                     if (!notif.getIdReminder().getIsDeleted())
//                         pass = true;
//                 } else {
//                     pass = true;
//                 }
//             }
//             if (pass) {
//                 if (notif.getIdUser().equals(user)) {
//                     notif.setIsDeleted(true);
//                     notif.setDeletedAt(LocalDate.now());
//                     return Optional.of(notifikasiRepository.save(notif));
//                 }
//             }
//         }
//         return Optional.empty();
//     }

//     public ArrayList<String> deleteNotifikasiListByUser(String idUser, List<String> notifikasiIdList) {
//         ArrayList<String> deletedNotifikasi = new ArrayList<String>();
//         for (String idNotifikasi : notifikasiIdList) {
//             if (deleteNotifikasisByUser(idUser, idNotifikasi).isPresent())
//                 deletedNotifikasi.add(idNotifikasi);
//         }
//         return deletedNotifikasi;
//     }

//     public Optional<Notifikasi> readNotifikasisByUser(String idUser, String notifikasiId) {
//         User user = userService.findUserById(idUser).get();
//         Optional<Notifikasi> optionalNotifikasi = getNotifikasiById(notifikasiId);
//         if (optionalNotifikasi.isPresent()) {
//             Notifikasi notif = optionalNotifikasi.get();
//             Boolean pass = false;
//             if (!notif.getIsDeleted()) {
//                 if (notif.getJenisNotifikasi().equals(JenisNotifikasi.AGENDA)) {
//                     if (!notif.getIdAgenda().getIsDeleted())
//                         pass = true;
//                 } else if (notif.getJenisNotifikasi().equals(JenisNotifikasi.REMINDER)) {
//                     if (!notif.getIdReminder().getIsDeleted())
//                         pass = true;
//                 } else {
//                     pass = true;
//                 }
//             }
//             if (pass) {
//                 if (notif.getIdUser().equals(user)) {
//                     notif.setIsRead(true);
//                     return Optional.of(notifikasiRepository.save(notif));
//                 }
//             }
//         }
//         return Optional.empty();
//     }

//     public ArrayList<Notifikasi> getAllNotifikasi() {
//         ArrayList<Notifikasi> result = new ArrayList<Notifikasi>();
//         for (Notifikasi notif : notifikasiRepository.findAll()) {
//             Boolean pass = false;
//             if (!notif.getIsDeleted()) {
//                 if (notif.getJenisNotifikasi().equals(JenisNotifikasi.AGENDA)) {
//                     if (!notif.getIdAgenda().getIsDeleted() && !notif.getIdAgenda().getIdKontak().getIsDeleted())
//                         pass = true;
//                 } else if (notif.getJenisNotifikasi().equals(JenisNotifikasi.REMINDER)) {
//                     if (!notif.getIdReminder().getIsDeleted() && !notif.getIdReminder().getIdKontak().getIsDeleted())
//                         pass = true;
//                 } else {
//                     pass = true;
//                 }
//             }
//             if (pass)
//                 result.add(notif);
//         }
//         return result;
//     }

//     public Optional<Notifikasi> getNotifikasiById(String idNotifikasi) {
//         Optional<Notifikasi> optionalNotifikasi = notifikasiRepository.findById(idNotifikasi);
//         if (optionalNotifikasi.isPresent()) {
//             if (!optionalNotifikasi.get().getIsDeleted()) {
//                 Notifikasi notif = optionalNotifikasi.get();
//                 Boolean pass = false;
//                 if (!notif.getIsDeleted()) {
//                     if (notif.getJenisNotifikasi().equals(JenisNotifikasi.AGENDA)) {
//                         if (!notif.getIdAgenda().getIsDeleted() && !notif.getIdAgenda().getIdKontak().getIsDeleted())
//                             pass = true;
//                     } else if (notif.getJenisNotifikasi().equals(JenisNotifikasi.REMINDER)) {
//                         if (!notif.getIdReminder().getIsDeleted()
//                                 && !notif.getIdReminder().getIdKontak().getIsDeleted())
//                             pass = true;
//                     } else {
//                         pass = true;
//                     }
//                 }
//                 if (pass)
//                     return Optional.of(notif);
//             }
//         }
//         return Optional.empty();
//     }
// }
