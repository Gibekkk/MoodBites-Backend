package com.kritz.restfulapi.controller;

import java.util.Optional;
import java.util.ArrayList;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.kritz.restfulapi.dto.BahanDTO;
import com.kritz.restfulapi.dto.RestockDTO;
import com.kritz.restfulapi.model.Bahan;
import com.kritz.restfulapi.model.enums.Level;
import com.kritz.restfulapi.model.Toko;
import com.kritz.restfulapi.model.Session;
import com.kritz.restfulapi.service.LoginService;
import com.kritz.restfulapi.service.BahanService;
import com.kritz.restfulapi.util.ErrorMessage;
import com.kritz.restfulapi.util.HTTPCode;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping(value = "${storage.api-prefix}/toko/bahan")
public class BahanController {
    @Autowired
    private LoginService loginService;

    @Autowired
    private BahanService bahanService;

    private Object data = "";


    @PostMapping("/restock")
    public ResponseEntity<Object> restock(HttpServletRequest request, @RequestBody RestockDTO restockDTO) {
        String sessionToken = request.getHeader("Token");
        HTTPCode httpCode = HTTPCode.OK;
        try {
            restockDTO.checkDTO();
            Optional<Session> sessionOpt = loginService.findSessionBySessionToken(sessionToken);
            if (sessionOpt.isPresent()) {
                Session session = sessionOpt.get();
                if (session.getIdLogin().getLevel() == Level.TOKO) {
                    Toko toko = session.getIdLogin().getIdToko();
                    ArrayList<Map<String, Object>> restockResults = bahanService.restockBahan(toko, restockDTO);
                    data = Map.of(
                            "restockResults", restockResults);
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

    @GetMapping
    public ResponseEntity<Object> getAllBahan(HttpServletRequest request) {
        String sessionToken = request.getHeader("Token");
        HTTPCode httpCode = HTTPCode.OK;
        try {
            Optional<Session> sessionOpt = loginService.findSessionBySessionToken(sessionToken);
            if (sessionOpt.isPresent()) {
                Session session = sessionOpt.get();
                if (session.getIdLogin().getLevel() == Level.TOKO) {
                    Toko toko = session.getIdLogin().getIdToko();
                    ArrayList<Object> bahans = new ArrayList<>();
                    for (Bahan bahan : toko.getListBahan()) {
                        if (bahan.getDeletedAt() == null)
                            bahans.add(Map.of(
                                    "id", bahan.getId(),
                                    "namaBahan", bahan.getNama(),
                                    "stock", bahan.getIdStock().getStock(),
                                    "deskripsi", bahan.getDeskripsi(),
                                    "gambarBahan", bahanService.getImage(bahan),
                                    "satuan", bahan.getSatuanBahan().toString()));
                    }
                    data = bahans;
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

    @GetMapping("/{idBahan}")
    public ResponseEntity<Object> getBahanById(HttpServletRequest request, @PathVariable String idBahan) {
        String sessionToken = request.getHeader("Token");
        HTTPCode httpCode = HTTPCode.OK;
        try {
            Optional<Session> sessionOpt = loginService.findSessionBySessionToken(sessionToken);
            if (sessionOpt.isPresent()) {
                Session session = sessionOpt.get();
                if (session.getIdLogin().getLevel() == Level.TOKO) {
                    Toko toko = session.getIdLogin().getIdToko();
                    boolean found = false;
                    for (Bahan bahan : toko.getListBahan()) {
                        if (bahan.getId().equals(idBahan) && bahan.getDeletedAt() == null){
                            found = true;
                            data = Map.of(
                                    "id", bahan.getId(),
                                    "namaBahan", bahan.getNama(),
                                    "stock", bahan.getIdStock().getStock(),
                                    "deskripsi", bahan.getDeskripsi(),
                                    "gambarBahan", bahanService.getImage(bahan),
                                    "satuan", bahan.getSatuanBahan().toString());
                                    break;
                            }

                    }
                    if(!found) {
                        httpCode = HTTPCode.NOT_FOUND;
                        data = new ErrorMessage(httpCode, "Bahan dengan ID " + idBahan + " tidak ditemukan.");
                    }
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

    @PatchMapping("/{idBahan}")
    public ResponseEntity<Object> editBahanById(HttpServletRequest request, @PathVariable String idBahan,
            @ModelAttribute BahanDTO bahanDTO) {
        String sessionToken = request.getHeader("Token");
        HTTPCode httpCode = HTTPCode.OK;
        try {
            Optional<Session> sessionOpt = loginService.findSessionBySessionToken(sessionToken);
            if (sessionOpt.isPresent()) {
                Session session = sessionOpt.get();
                if (session.getIdLogin().getLevel() == Level.TOKO) {
                    Toko toko = session.getIdLogin().getIdToko();
                    boolean found = false;
                    for (Bahan bahan : toko.getListBahan()) {
                        if (bahan.getId().equals(idBahan) && bahan.getDeletedAt() == null) {
                            found = true;
                            bahan = bahanService.editBahan(bahanDTO, bahan);
                            data = Map.of(
                                    "id", bahan.getId(),
                                    "namaBahan", bahan.getNama(),
                                    "stock", bahan.getIdStock().getStock(),
                                    "deskripsi", bahan.getDeskripsi(),
                                    "gambarBahan", bahanService.getImage(bahan),
                                    "satuan", bahan.getSatuanBahan().toString());
                            break;
                        }
                    }
                    if(!found) {
                        httpCode = HTTPCode.NOT_FOUND;
                        data = new ErrorMessage(httpCode, "Bahan dengan ID " + idBahan + " tidak ditemukan.");
                    }
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

    @DeleteMapping("/{idBahan}")
    public ResponseEntity<Object> deleteBahanById(HttpServletRequest request, @PathVariable String idBahan) {
        String sessionToken = request.getHeader("Token");
        HTTPCode httpCode = HTTPCode.OK;
        try {
            Optional<Session> sessionOpt = loginService.findSessionBySessionToken(sessionToken);
            if (sessionOpt.isPresent()) {
                Session session = sessionOpt.get();
                if (session.getIdLogin().getLevel() == Level.TOKO) {
                    Toko toko = session.getIdLogin().getIdToko();
                    boolean found = false;
                    for (Bahan bahan : toko.getListBahan()) {
                        if (bahan.getId().equals(idBahan) && bahan.getDeletedAt() == null) {
                            found = true;
                            bahanService.flagDeleteBahan(bahan);
                            data = Map.of(
                                    "message", "Bahan dengan ID " + idBahan + " berhasil dihapus");
                            break;
                        }
                    }
                    if(!found) {
                        httpCode = HTTPCode.NOT_FOUND;
                        data = new ErrorMessage(httpCode, "Bahan dengan ID " + idBahan + " tidak ditemukan.");
                    }
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

    @PostMapping
    public ResponseEntity<Object> addBahan(HttpServletRequest request, @ModelAttribute BahanDTO bahanDTO) {
        String sessionToken = request.getHeader("Token");
        HTTPCode httpCode = HTTPCode.OK;
        try {
            bahanDTO.checkDTO();
            Optional<Session> sessionOpt = loginService.findSessionBySessionToken(sessionToken);
            if (sessionOpt.isPresent()) {
                Session session = sessionOpt.get();
                if (session.getIdLogin().getLevel() == Level.TOKO) {
                    Toko toko = session.getIdLogin().getIdToko();
                    Bahan bahan = bahanService.addBahan(bahanDTO, toko);
                    data = Map.of(
                            "idBahan", bahan.getId());
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
