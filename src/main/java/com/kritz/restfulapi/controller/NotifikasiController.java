// package com.kritz.restfulapi.controller;

// import java.util.ArrayList;
// import java.util.Map;
// import java.util.Optional;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.MediaType;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.CrossOrigin;
// import org.springframework.web.bind.annotation.DeleteMapping;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import com.kritz.restfulapi.util.ErrorMessage;
// import com.kritz.restfulapi.util.HTTPCode;

// import jakarta.servlet.http.HttpServletRequest;

// import com.kritz.restfulapi.service.LoginService;
// import com.kritz.restfulapi.service.NotifikasiService;
// import com.kritz.restfulapi.dto.HapusNotifikasiDTO;
// import com.kritz.restfulapi.model.Notifikasi;
// import com.kritz.restfulapi.service.AgendaService;
// import com.kritz.restfulapi.service.ReminderService;

// @RestController
// @CrossOrigin
// @RequestMapping(value = "${storage.api-prefix}/notification")
// public class NotifikasiController {

// @Autowired
// private LoginService loginService;

// @Autowired
// private NotifikasiService notifikasiService;

// @Autowired
// private AgendaService agendaService;

// @Autowired
// private ReminderService reminderService;

// private Object data = "";

// @GetMapping
// public ResponseEntity<Object> getNotifications(HttpServletRequest request) {
// String sessionToken = request.getHeader("Token");
// HTTPCode httpCode = HTTPCode.OK;
// try {
// if (loginService.checkSessionAlive(sessionToken)) {
// if (loginService.checkSessionIsUser(sessionToken)) {
// data = notifikasiService
// .getNotifikasiDetailsByUser(loginService.getLoginSession(sessionToken).getId());
// } else {
// httpCode = HTTPCode.FORBIDDEN;
// data = new ErrorMessage(httpCode, "Akses Ditolak");
// }
// } else {
// httpCode = HTTPCode.BAD_REQUEST;
// data = new ErrorMessage(httpCode, "Pemeriksaan Autentikasi Gagal");
// }
// } catch (IllegalArgumentException e) {
// httpCode = HTTPCode.BAD_REQUEST;
// data = new ErrorMessage(httpCode, e.getMessage());
// } catch (Exception e) {
// httpCode = HTTPCode.INTERNAL_SERVER_ERROR;
// data = new ErrorMessage(httpCode, e.getMessage());
// }

// return ResponseEntity
// .status(httpCode.getStatus())
// .contentType(MediaType.APPLICATION_JSON)
// .body(data);
// }

// @GetMapping("/notify")
// public ResponseEntity<Object> refreshNotifications() {
// HTTPCode httpCode = HTTPCode.OK;
// try {
// reminderService.notifyReminders();
// agendaService.notifyAgendas();
// data = Map.of(
// "status", "Notifikasi Diperbarui");
// } catch (IllegalArgumentException e) {
// httpCode = HTTPCode.BAD_REQUEST;
// data = new ErrorMessage(httpCode, e.getMessage());
// } catch (Exception e) {
// httpCode = HTTPCode.INTERNAL_SERVER_ERROR;
// data = new ErrorMessage(httpCode, e.getMessage());
// }

// return ResponseEntity
// .status(httpCode.getStatus())
// .contentType(MediaType.APPLICATION_JSON)
// .body(data);
// }

// @DeleteMapping
// public ResponseEntity<Object> deleteNotification(HttpServletRequest request,
// @RequestBody HapusNotifikasiDTO hapusNotifikasiDTO) {
// String sessionToken = request.getHeader("Token");
// HTTPCode httpCode = HTTPCode.OK;
// try {
// if (loginService.checkSessionAlive(sessionToken)) {
// if (loginService.checkSessionIsUser(sessionToken)) {
// if(hapusNotifikasiDTO.checkDTO()){
// ArrayList<String> deletedNotifikasi = notifikasiService
// .deleteNotifikasiListByUser(loginService.getLoginSession(sessionToken).getId(),
// hapusNotifikasiDTO.getListNotifikasi());
// if (deletedNotifikasi.size() > 0) {
// data = Map.of("deletedNotifikasi", deletedNotifikasi);
// } else {
// httpCode = HTTPCode.NOT_FOUND;
// data = new ErrorMessage(httpCode, "Tidak Ada Notifikasi Yang Ditemukan");
// }
// } else {
// httpCode = HTTPCode.BAD_REQUEST;
// data = new ErrorMessage(httpCode, "Form Yang Diberikan Tidak Valid");
// }
// } else {
// httpCode = HTTPCode.FORBIDDEN;
// data = new ErrorMessage(httpCode, "Akses Ditolak");
// }
// } else {
// httpCode = HTTPCode.BAD_REQUEST;
// data = new ErrorMessage(httpCode, "Pemeriksaan Autentikasi Gagal");
// }
// } catch (IllegalArgumentException e) {
// httpCode = HTTPCode.BAD_REQUEST;
// data = new ErrorMessage(httpCode, e.getMessage());
// } catch (Exception e) {
// httpCode = HTTPCode.INTERNAL_SERVER_ERROR;
// data = new ErrorMessage(httpCode, e.getMessage());
// }

// return ResponseEntity
// .status(httpCode.getStatus())
// .contentType(MediaType.APPLICATION_JSON)
// .body(data);
// }

// @GetMapping("/{idNotification}")
// public ResponseEntity<Object> getNotificationById(HttpServletRequest request,
// @PathVariable String idNotification) {
// String sessionToken = request.getHeader("Token");
// HTTPCode httpCode = HTTPCode.OK;
// try {
// if (loginService.checkSessionAlive(sessionToken)) {
// if (loginService.checkSessionIsUser(sessionToken)) {
// reminderService.notifyReminders();
// agendaService.notifyAgendas();
// Optional<Notifikasi> notifikasiOptional = notifikasiService
// .readNotifikasisByUser(loginService.getLoginSession(sessionToken).getId(),
// idNotification);
// if (notifikasiOptional.isPresent()) {
// Notifikasi notifikasi = notifikasiOptional.get();
// if
// (notifikasi.getIdUser().getId().equals(loginService.getLoginSession(sessionToken).getId()))
// {
// data = Map.of(
// "id", notifikasi.getId(),
// "message", notifikasi.getDeskripsiNotifikasi(),
// "dateCreated", notifikasi.getCreatedAt(),
// "title", notifikasi.getNamaNotifikasi(),
// "isClicked", notifikasi.getIsRead(),
// "jenisNotifikasi", notifikasi.getJenisNotifikasi().toString(),
// "idAgenda", notifikasi.getIdAgenda() == null ? "" :
// notifikasi.getIdAgenda().getId(),
// "idReminder", notifikasi.getIdReminder() == null ? "" :
// notifikasi.getIdReminder().getId());
// } else {
// httpCode = HTTPCode.BAD_REQUEST;
// data = new ErrorMessage(httpCode, "User Tidak Cocok");
// }
// } else {
// httpCode = HTTPCode.NOT_FOUND;
// data = new ErrorMessage(httpCode, "Notifikasi Tidak Ditemukan");
// }
// } else {
// httpCode = HTTPCode.FORBIDDEN;
// data = new ErrorMessage(httpCode, "Akses Ditolak");
// }
// } else {
// httpCode = HTTPCode.BAD_REQUEST;
// data = new ErrorMessage(httpCode, "Pemeriksaan Autentikasi Gagal");
// }
// } catch (IllegalArgumentException e) {
// httpCode = HTTPCode.BAD_REQUEST;
// data = new ErrorMessage(httpCode, e.getMessage());
// } catch (Exception e) {
// httpCode = HTTPCode.INTERNAL_SERVER_ERROR;
// data = new ErrorMessage(httpCode, e.getMessage());
// }

// return ResponseEntity
// .status(httpCode.getStatus())
// .contentType(MediaType.APPLICATION_JSON)
// .body(data);
// }
// }
