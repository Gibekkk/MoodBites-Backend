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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kritz.restfulapi.dto.EditPesananDTO;
import com.kritz.restfulapi.dto.MenuDTO;
import com.kritz.restfulapi.dto.PaymentDTO;
import com.kritz.restfulapi.dto.PesananDTO;
import com.kritz.restfulapi.model.Menu;
import com.kritz.restfulapi.model.MenuPenjualan;
import com.kritz.restfulapi.model.Penjualan;
import com.kritz.restfulapi.model.enums.Level;
import com.kritz.restfulapi.model.enums.TipePembayaran;
import com.kritz.restfulapi.model.Toko;
import com.kritz.restfulapi.model.Session;
import com.kritz.restfulapi.service.LoginService;
import com.kritz.restfulapi.service.MenuService;
import com.kritz.restfulapi.service.BahanService;
import com.kritz.restfulapi.util.ErrorMessage;
import com.kritz.restfulapi.util.HTTPCode;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping(value = "${storage.api-prefix}/toko/menu")
public class MenuController {
    @Autowired
    private LoginService loginService;

    @Autowired
    private MenuService menuService;

    @Autowired
    private BahanService bahanService;

    private Object data = "";

    @GetMapping
    public ResponseEntity<Object> getAllMenu(HttpServletRequest request) {
        String sessionToken = request.getHeader("Token");
        HTTPCode httpCode = HTTPCode.OK;
        try {
            Optional<Session> sessionOpt = loginService.findSessionBySessionToken(sessionToken);
            if (sessionOpt.isPresent()) {
                Session session = sessionOpt.get();
                if (session.getIdLogin().getLevel() == Level.TOKO) {
                    Toko toko = session.getIdLogin().getIdToko();
                    ArrayList<Object> menus = new ArrayList<>();
                    for (Menu menu : toko.getListMenu()) {
                        if (menu.getDeletedAt() == null) {
                            int stock = menuService.getMenuStock(menu);
                            menus.add(Map.of(
                                    "id", menu.getId(),
                                    "namaMenu", menu.getNama(),
                                    "harga", menu.getIdPricelist().getHarga(),
                                    "stock", stock == 100 ? "99+" : Integer.toString(stock),
                                    "deskripsi", menu.getDeskripsi(),
                                    "kategori", menu.getKategori().toString(),
                                    "gambarMenu", menuService.getImage(menu),
                                    "diskon", menu.getIdPricelist().getDiskon(),
                                    "bahan", menu.getListBahanResep().stream().map(bahanResep -> Map.of(
                                            "idBahanResep", bahanResep.getId(),
                                            "namaBahan", bahanResep.getIdBahan().getNama(),
                                            "jumlah", bahanResep.getJumlah(),
                                            "satuan", bahanResep.getIdBahan().getSatuanBahan().toString(),
                                            "gambarBahan", bahanService.getImage(bahanResep.getIdBahan()),
                                            "deskripsiBahan", bahanResep.getIdBahan().getDeskripsi())).toList(),
                                    "langkah", menu.getListLangkahResep().stream().map(langkahResep -> Map.of(
                                            "idLangkahResep", langkahResep.getId(),
                                            "deskripsiLangkah", langkahResep.getDeskripsi(),
                                            "urutan", langkahResep.getUrutan())).toList()));
                        }
                    }
                    data = menus;
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

    @GetMapping("/{idMenu}")
    public ResponseEntity<Object> getMenuById(HttpServletRequest request, @PathVariable String idMenu) {
        String sessionToken = request.getHeader("Token");
        HTTPCode httpCode = HTTPCode.OK;
        try {
            Optional<Session> sessionOpt = loginService.findSessionBySessionToken(sessionToken);
            if (sessionOpt.isPresent()) {
                Session session = sessionOpt.get();
                if (session.getIdLogin().getLevel() == Level.TOKO) {
                    Toko toko = session.getIdLogin().getIdToko();
                    boolean found = false;
                    for (Menu menu : toko.getListMenu()) {
                        if (menu.getId().equals(idMenu) && menu.getDeletedAt() == null) {
                            found = true;
                            int stock = menuService.getMenuStock(menu);
                            data = Map.of(
                                    "id", menu.getId(),
                                    "namaMenu", menu.getNama(),
                                    "harga", menu.getIdPricelist().getHarga(),
                                    "deskripsi", menu.getDeskripsi(),
                                    "stock", stock == 100 ? "99+" : Integer.toString(stock),
                                    "kategori", menu.getKategori().toString(),
                                    "gambarMenu", menuService.getImage(menu),
                                    "diskon", menu.getIdPricelist().getDiskon(),
                                    "bahan", menu.getListBahanResep().stream().map(bahanResep -> Map.of(
                                            "idBahanResep", bahanResep.getId(),
                                            "namaBahan", bahanResep.getIdBahan().getNama(),
                                            "jumlah", bahanResep.getJumlah(),
                                            "satuan", bahanResep.getIdBahan().getSatuanBahan().toString(),
                                            "gambarBahan", bahanService.getImage(bahanResep.getIdBahan()),
                                            "deskripsiBahan", bahanResep.getIdBahan().getDeskripsi())).toList(),
                                    "langkah", menu.getListLangkahResep().stream().map(langkahResep -> Map.of(
                                            "idLangkahResep", langkahResep.getId(),
                                            "deskripsiLangkah", langkahResep.getDeskripsi(),
                                            "urutan", langkahResep.getUrutan())).toList());
                            break;
                        }

                    }
                    if (!found) {
                        httpCode = HTTPCode.NOT_FOUND;
                        data = new ErrorMessage(httpCode, "Menu dengan ID " + idMenu + " tidak ditemukan.");
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

    @PatchMapping("/{idMenu}")
    public ResponseEntity<Object> editMenuById(HttpServletRequest request, @PathVariable String idMenu,
            @ModelAttribute MenuDTO menuDTO) {
        String sessionToken = request.getHeader("Token");
        HTTPCode httpCode = HTTPCode.OK;
        try {
            Optional<Session> sessionOpt = loginService.findSessionBySessionToken(sessionToken);
            if (sessionOpt.isPresent()) {
                Session session = sessionOpt.get();
                if (session.getIdLogin().getLevel() == Level.TOKO) {
                    Toko toko = session.getIdLogin().getIdToko();
                    boolean found = false;
                    for (Menu menu : toko.getListMenu()) {
                        if (menu.getId().equals(idMenu) && menu.getDeletedAt() == null) {
                            found = true;
                            menu = menuService.editMenu(menuDTO, menu, toko);
                            int stock = menuService.getMenuStock(menu);
                            data = Map.of(
                                    "id", menu.getId(),
                                    "namaMenu", menu.getNama(),
                                    "harga", menu.getIdPricelist().getHarga(),
                                    "deskripsi", menu.getDeskripsi(),
                                    "stock", stock == 100 ? "99+" : Integer.toString(stock),
                                    "kategori", menu.getKategori().toString(),
                                    "gambarMenu", menuService.getImage(menu),
                                    "diskon", menu.getIdPricelist().getDiskon(),
                                    "bahan", menu.getListBahanResep().stream().map(bahanResep -> Map.of(
                                            "idBahanResep", bahanResep.getId(),
                                            "namaBahan", bahanResep.getIdBahan().getNama(),
                                            "jumlah", bahanResep.getJumlah(),
                                            "satuan", bahanResep.getIdBahan().getSatuanBahan().toString(),
                                            "gambarBahan", bahanService.getImage(bahanResep.getIdBahan()),
                                            "deskripsiBahan", bahanResep.getIdBahan().getDeskripsi())).toList(),
                                    "langkah", menu.getListLangkahResep().stream().map(langkahResep -> Map.of(
                                            "idLangkahResep", langkahResep.getId(),
                                            "deskripsiLangkah", langkahResep.getDeskripsi(),
                                            "urutan", langkahResep.getUrutan())).toList());
                            break;
                        }
                    }
                    if (!found) {
                        httpCode = HTTPCode.NOT_FOUND;
                        data = new ErrorMessage(httpCode, "Menu dengan ID " + idMenu + " tidak ditemukan.");
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

    @DeleteMapping("/{idMenu}")
    public ResponseEntity<Object> deleteMenuById(HttpServletRequest request, @PathVariable String idMenu) {
        String sessionToken = request.getHeader("Token");
        HTTPCode httpCode = HTTPCode.OK;
        try {
            Optional<Session> sessionOpt = loginService.findSessionBySessionToken(sessionToken);
            if (sessionOpt.isPresent()) {
                Session session = sessionOpt.get();
                if (session.getIdLogin().getLevel() == Level.TOKO) {
                    Toko toko = session.getIdLogin().getIdToko();
                    boolean found = false;
                    for (Menu menu : toko.getListMenu()) {
                        if (menu.getId().equals(idMenu) && menu.getDeletedAt() == null) {
                            found = true;
                            menuService.flagDeleteMenu(menu);
                            data = Map.of(
                                    "message", "Menu dengan ID " + idMenu + " berhasil dihapus");
                            break;
                        }
                    }
                    if (!found) {
                        httpCode = HTTPCode.NOT_FOUND;
                        data = new ErrorMessage(httpCode, "Menu dengan ID " + idMenu + " tidak ditemukan.");
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

    @PostMapping("/cart")
    public ResponseEntity<Object> addMenuToCart(HttpServletRequest request, @RequestBody PesananDTO pesananDTO) {
        String sessionToken = request.getHeader("Token");
        HTTPCode httpCode = HTTPCode.OK;
        try {
            pesananDTO.checkDTO();
            Optional<Session> sessionOpt = loginService.findSessionBySessionToken(sessionToken);
            if (sessionOpt.isPresent()) {
                Session session = sessionOpt.get();
                if (session.getIdLogin().getLevel() == Level.TOKO) {
                    Toko toko = session.getIdLogin().getIdToko();
                    Optional<Menu> menuOpt = menuService.findByMenuAndToko(pesananDTO.getIdMenu(), toko);
                    if (menuOpt.isPresent()) {
                        Menu menu = menuOpt.get();
                        if (menuService.getMenuStock(menu) >= pesananDTO.getJumlah()) {
                            Penjualan penjualan = menuService.addPesanan(toko, pesananDTO);
                            data = Map.of(
                                    "idPenjualan", penjualan.getId(),
                                    "statusPenjualan", penjualan.getStatusPenjualan().toString(),
                                    "listMenu", penjualan.getListMenuPenjualan().stream().map(menuPenjualan -> Map.of(
                                            "idItem", menuPenjualan.getId(),
                                            "idMenu", menuPenjualan.getIdMenu().getId(),
                                            "namaMenu", menuPenjualan.getIdMenu().getNama(),
                                            "jumlah", menuPenjualan.getJumlah(),
                                            "totalHarga", menuPenjualan.getHarga() * menuPenjualan.getJumlah(),
                                            "diskon", menuPenjualan.getDiskon(),
                                            "komentar", Optional.ofNullable(menuPenjualan.getKomentar()).orElse("")))
                                            .toList(),
                                    "totalBayar", penjualan.getTotalBayar(),
                                    "totalHarga", penjualan.getTotalHarga(),
                                    "diskon", penjualan.getDiskon(),
                                    "tipePembayaran",
                                    Optional.ofNullable(penjualan.getTipePembayaran()).map(TipePembayaran::toString)
                                            .orElse(""),
                                    "createdAt", penjualan.getCreatedAt(),
                                    "editedAt", penjualan.getEditedAt());
                        } else {
                            httpCode = HTTPCode.BAD_REQUEST;
                            data = new ErrorMessage(httpCode, "Stok menu tidak mencukupi untuk pesanan");
                        }
                    } else {
                        httpCode = HTTPCode.NOT_FOUND;
                        data = new ErrorMessage(httpCode, "Menu tidak ditemukan");
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

    @PutMapping("/cart/{itemId}")
    public ResponseEntity<Object> editCartItem(HttpServletRequest request, @RequestBody EditPesananDTO editPesananDTO,
            @PathVariable String itemId) {
        String sessionToken = request.getHeader("Token");
        HTTPCode httpCode = HTTPCode.OK;
        try {
            editPesananDTO.checkDTO();
            Optional<Session> sessionOpt = loginService.findSessionBySessionToken(sessionToken);
            if (sessionOpt.isPresent()) {
                Session session = sessionOpt.get();
                if (session.getIdLogin().getLevel() == Level.TOKO) {
                    Toko toko = session.getIdLogin().getIdToko();
                    Optional<Penjualan> penjualanOpt = menuService.findCurrentCart(toko);
                    if (penjualanOpt.isPresent()) {
                        Penjualan penjualan = penjualanOpt.get();
                        MenuPenjualan itemPenjualan = penjualan.getListMenuPenjualan().stream()
                                .filter(item -> item.getId().equals(itemId))
                                .findFirst()
                                .orElseThrow(() -> new IllegalArgumentException(
                                        "Item dengan ID " + itemId + " tidak ditemukan di keranjang."));
                        Menu menu = itemPenjualan.getIdMenu();
                        if (menuService.getMenuStock(menu) >= editPesananDTO.getJumlah() - itemPenjualan.getJumlah()) {
                            penjualan = menuService.editPesanan(itemPenjualan, editPesananDTO);
                            data = Map.of(
                                    "idPenjualan", penjualan.getId(),
                                    "statusPenjualan", penjualan.getStatusPenjualan().toString(),
                                    "listMenu", penjualan.getListMenuPenjualan().stream().map(menuPenjualan -> Map.of(
                                            "idItem", menuPenjualan.getId(),
                                            "idMenu", menuPenjualan.getIdMenu().getId(),
                                            "namaMenu", menuPenjualan.getIdMenu().getNama(),
                                            "jumlah", menuPenjualan.getJumlah(),
                                            "totalHarga", menuPenjualan.getHarga() * menuPenjualan.getJumlah(),
                                            "diskon", menuPenjualan.getDiskon(),
                                            "komentar", Optional.ofNullable(menuPenjualan.getKomentar()).orElse("")))
                                            .toList(),
                                    "totalBayar", penjualan.getTotalBayar(),
                                    "totalHarga", penjualan.getTotalHarga(),
                                    "diskon", penjualan.getDiskon(),
                                    "tipePembayaran",
                                    Optional.ofNullable(penjualan.getTipePembayaran()).map(TipePembayaran::toString)
                                            .orElse(""),
                                    "createdAt", penjualan.getCreatedAt(),
                                    "editedAt", penjualan.getEditedAt());
                        } else {
                            httpCode = HTTPCode.BAD_REQUEST;
                            data = new ErrorMessage(httpCode, "Stok menu tidak mencukupi untuk pesanan");
                        }
                    } else {
                        httpCode = HTTPCode.NOT_FOUND;
                        data = new ErrorMessage(httpCode, "Item Cart tidak ditemukan");
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

    @DeleteMapping("/cart/{itemId}")
    public ResponseEntity<Object> deleteCartItem(HttpServletRequest request, @PathVariable String itemId) {
        String sessionToken = request.getHeader("Token");
        HTTPCode httpCode = HTTPCode.OK;
        try {
            Optional<Session> sessionOpt = loginService.findSessionBySessionToken(sessionToken);
            if (sessionOpt.isPresent()) {
                Session session = sessionOpt.get();
                if (session.getIdLogin().getLevel() == Level.TOKO) {
                    Toko toko = session.getIdLogin().getIdToko();
                    Optional<Penjualan> penjualanOpt = menuService.findCurrentCart(toko);
                    if (penjualanOpt.isPresent()) {
                        Penjualan penjualan = penjualanOpt.get();
                        MenuPenjualan itemPenjualan = penjualan.getListMenuPenjualan().stream()
                                .filter(item -> item.getId().equals(itemId))
                                .findFirst()
                                .orElseThrow(() -> new IllegalArgumentException(
                                        "Item dengan ID " + itemId + " tidak ditemukan di keranjang."));
                        penjualan = menuService.deletePesanan(itemPenjualan);
                        data = Map.of("idPenjualan", penjualan.getId(),
                                "statusPenjualan", penjualan.getStatusPenjualan().toString(),
                                "listMenu", penjualan.getListMenuPenjualan().stream().map(menuPenjualan -> Map.of(
                                        "idItem", menuPenjualan.getId(),
                                        "idMenu", menuPenjualan.getIdMenu().getId(),
                                        "namaMenu", menuPenjualan.getIdMenu().getNama(),
                                        "jumlah", menuPenjualan.getJumlah(),
                                        "totalHarga", menuPenjualan.getHarga() * menuPenjualan.getJumlah(),
                                        "diskon", menuPenjualan.getDiskon(),
                                        "komentar", Optional.ofNullable(menuPenjualan.getKomentar()).orElse("")))
                                        .toList(),
                                "totalBayar", penjualan.getTotalBayar(),
                                "totalHarga", penjualan.getTotalHarga(),
                                "diskon", penjualan.getDiskon(),
                                "tipePembayaran",
                                Optional.ofNullable(penjualan.getTipePembayaran()).map(TipePembayaran::toString)
                                        .orElse(""),
                                "createdAt", penjualan.getCreatedAt(),
                                "editedAt", penjualan.getEditedAt());
                    } else {
                        httpCode = HTTPCode.NOT_FOUND;
                        data = new ErrorMessage(httpCode, "Item Cart tidak ditemukan");
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

    @GetMapping("/cart/{itemId}")
    public ResponseEntity<Object> getItemCartById(HttpServletRequest request, @PathVariable String itemId) {
        String sessionToken = request.getHeader("Token");
        HTTPCode httpCode = HTTPCode.OK;
        try {
            Optional<Session> sessionOpt = loginService.findSessionBySessionToken(sessionToken);
            if (sessionOpt.isPresent()) {
                Session session = sessionOpt.get();
                if (session.getIdLogin().getLevel() == Level.TOKO) {
                    Toko toko = session.getIdLogin().getIdToko();
                    Optional<Penjualan> penjualanOpt = menuService.findCurrentCart(toko);
                    if (penjualanOpt.isPresent()) {
                        Penjualan penjualan = penjualanOpt.get();
                        MenuPenjualan itemPenjualan = penjualan.getListMenuPenjualan().stream()
                                .filter(item -> item.getId().equals(itemId))
                                .findFirst()
                                .orElseThrow(() -> new IllegalArgumentException(
                                        "Item dengan ID " + itemId + " tidak ditemukan di keranjang."));
                        data = Map.of("idItem", itemPenjualan.getId(),
                                "idMenu", itemPenjualan.getIdMenu().getId(),
                                "namaMenu", itemPenjualan.getIdMenu().getNama(),
                                "jumlah", itemPenjualan.getJumlah(),
                                "komentar", Optional.ofNullable(itemPenjualan.getKomentar()).orElse(""));
                    } else {
                        httpCode = HTTPCode.NOT_FOUND;
                        data = new ErrorMessage(httpCode, "Item Cart tidak ditemukan");
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

    @GetMapping("/cart")
    public ResponseEntity<Object> getCartItems(HttpServletRequest request) {
        String sessionToken = request.getHeader("Token");
        HTTPCode httpCode = HTTPCode.OK;
        try {
            Optional<Session> sessionOpt = loginService.findSessionBySessionToken(sessionToken);
            if (sessionOpt.isPresent()) {
                Session session = sessionOpt.get();
                if (session.getIdLogin().getLevel() == Level.TOKO) {
                    Toko toko = session.getIdLogin().getIdToko();
                    Optional<Penjualan> penjualanOpt = menuService.findCurrentCartNotPurchased(toko);
                    if (penjualanOpt.isPresent()) {
                        Penjualan penjualan = penjualanOpt.get();
                        data = Map.of("idPenjualan", penjualan.getId(),
                                "statusPenjualan", penjualan.getStatusPenjualan().toString(),
                                "listMenu", penjualan.getListMenuPenjualan().stream().map(menuPenjualan -> Map.of(
                                        "idItem", menuPenjualan.getId(),
                                        "idMenu", menuPenjualan.getIdMenu().getId(),
                                        "namaMenu", menuPenjualan.getIdMenu().getNama(),
                                        "jumlah", menuPenjualan.getJumlah(),
                                        "komentar", Optional.ofNullable(menuPenjualan.getKomentar()).orElse("")))
                                        .toList(),
                                "totalBayar", penjualan.getTotalBayar(),
                                "totalHarga", penjualan.getTotalHarga(),
                                "diskon", penjualan.getDiskon(),
                                "tipePembayaran",
                                Optional.ofNullable(penjualan.getTipePembayaran()).map(TipePembayaran::toString)
                                        .orElse(""),
                                "createdAt", penjualan.getCreatedAt(),
                                "editedAt", penjualan.getEditedAt());
                    } else {
                        httpCode = HTTPCode.NOT_FOUND;
                        data = new ErrorMessage(httpCode, "Item Cart tidak ditemukan");
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
    public ResponseEntity<Object> addMenu(HttpServletRequest request, @ModelAttribute MenuDTO menuDTO) {
        String sessionToken = request.getHeader("Token");
        HTTPCode httpCode = HTTPCode.OK;
        try {
            menuDTO.checkDTO();
            Optional<Session> sessionOpt = loginService.findSessionBySessionToken(sessionToken);
            if (sessionOpt.isPresent()) {
                Session session = sessionOpt.get();
                if (session.getIdLogin().getLevel() == Level.TOKO) {
                    Toko toko = session.getIdLogin().getIdToko();
                    Menu menu = menuService.addMenu(toko, menuDTO);
                    data = Map.of(
                            "idMenu", menu.getId());
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

    @PostMapping("/cart/backToCart")
    public ResponseEntity<Object> backToCart(HttpServletRequest request) {
        String sessionToken = request.getHeader("Token");
        HTTPCode httpCode = HTTPCode.OK;
        try {
            Optional<Session> sessionOpt = loginService.findSessionBySessionToken(sessionToken);
            if (sessionOpt.isPresent()) {
                Session session = sessionOpt.get();
                if (session.getIdLogin().getLevel() == Level.TOKO) {
                    Toko toko = session.getIdLogin().getIdToko();
                    Optional<Penjualan> penjualanOpt = menuService.findCurrentCartPayment(toko);
                    if (penjualanOpt.isPresent()) {
                        Penjualan penjualan = penjualanOpt.get();
                        penjualan = menuService.cartBackToCart(penjualan);
                        data = Map.of("idPenjualan", penjualan.getId(),
                                "statusPenjualan", penjualan.getStatusPenjualan().toString(),
                                "listMenu", penjualan.getListMenuPenjualan().stream().map(menuPenjualan -> Map.of(
                                        "idItem", menuPenjualan.getId(),
                                        "idMenu", menuPenjualan.getIdMenu().getId(),
                                        "namaMenu", menuPenjualan.getIdMenu().getNama(),
                                        "jumlah", menuPenjualan.getJumlah(),
                                        "komentar", Optional.ofNullable(menuPenjualan.getKomentar()).orElse("")))
                                        .toList(),
                                "totalBayar", penjualan.getTotalBayar(),
                                "totalHarga", penjualan.getTotalHarga(),
                                "diskon", penjualan.getDiskon(),
                                "tipePembayaran",
                                Optional.ofNullable(penjualan.getTipePembayaran()).map(TipePembayaran::toString)
                                        .orElse(""),
                                "createdAt", penjualan.getCreatedAt(),
                                "editedAt", penjualan.getEditedAt());
                    } else {
                        httpCode = HTTPCode.NOT_FOUND;
                        data = new ErrorMessage(httpCode, "Item Cart tidak ditemukan");
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

    @PostMapping("/cart/payment")
    public ResponseEntity<Object> toPayment(HttpServletRequest request) {
        String sessionToken = request.getHeader("Token");
        HTTPCode httpCode = HTTPCode.OK;
        try {
            Optional<Session> sessionOpt = loginService.findSessionBySessionToken(sessionToken);
            if (sessionOpt.isPresent()) {
                Session session = sessionOpt.get();
                if (session.getIdLogin().getLevel() == Level.TOKO) {
                    Toko toko = session.getIdLogin().getIdToko();
                    Optional<Penjualan> penjualanOpt = menuService.findCurrentCart(toko);
                    if (penjualanOpt.isPresent()) {
                        Penjualan penjualan = penjualanOpt.get();
                        penjualan = menuService.cartToPayment(penjualan);
                        data = Map.of("idPenjualan", penjualan.getId(),
                                "statusPenjualan", penjualan.getStatusPenjualan().toString(),
                                "listMenu", penjualan.getListMenuPenjualan().stream().map(menuPenjualan -> Map.of(
                                        "idItem", menuPenjualan.getId(),
                                        "idMenu", menuPenjualan.getIdMenu().getId(),
                                        "namaMenu", menuPenjualan.getIdMenu().getNama(),
                                        "jumlah", menuPenjualan.getJumlah(),
                                        "komentar", Optional.ofNullable(menuPenjualan.getKomentar()).orElse("")))
                                        .toList(),
                                "totalBayar", penjualan.getTotalBayar(),
                                "totalHarga", penjualan.getTotalHarga(),
                                "diskon", penjualan.getDiskon(),
                                "tipePembayaran",
                                Optional.ofNullable(penjualan.getTipePembayaran()).map(TipePembayaran::toString)
                                        .orElse(""),
                                "createdAt", penjualan.getCreatedAt(),
                                "editedAt", penjualan.getEditedAt());
                    } else {
                        httpCode = HTTPCode.NOT_FOUND;
                        data = new ErrorMessage(httpCode, "Item Cart tidak ditemukan");
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

    @PostMapping("/cart/completePayment")
    public ResponseEntity<Object> completePayment(HttpServletRequest request, @RequestBody PaymentDTO paymentDTO) {
        String sessionToken = request.getHeader("Token");
        HTTPCode httpCode = HTTPCode.OK;
        try {
            paymentDTO.checkDTO();
            Optional<Session> sessionOpt = loginService.findSessionBySessionToken(sessionToken);
            if (sessionOpt.isPresent()) {
                Session session = sessionOpt.get();
                if (session.getIdLogin().getLevel() == Level.TOKO) {
                    Toko toko = session.getIdLogin().getIdToko();
                    Optional<Penjualan> penjualanOpt = menuService.findCurrentCartPayment(toko);
                    if (penjualanOpt.isPresent()) {
                        Penjualan penjualan = penjualanOpt.get();
                        if (penjualan.getTotalHarga() > paymentDTO.getTotalBayar()) {
                            throw new IllegalArgumentException("Total Bayar tidak mencukupi jumlah yang harus dibayar");
                        }
                        penjualan = menuService.completePayment(penjualan, paymentDTO);
                        data = Map.ofEntries(
                                Map.entry("idPenjualan", penjualan.getId()),
                                Map.entry("statusPenjualan", penjualan.getStatusPenjualan().toString()),
                                Map.entry("listMenu", penjualan
                                        .getListMenuPenjualan().stream().map(menuPenjualan -> Map.of(
                                                "idItem", menuPenjualan.getId(),
                                                "idMenu", menuPenjualan.getIdMenu().getId(),
                                                "namaMenu", menuPenjualan.getIdMenu().getNama(),
                                                "jumlah", menuPenjualan.getJumlah(),
                                                "totalHarga",
                                                (menuPenjualan.getHarga() * menuPenjualan.getJumlah())
                                                        - ((menuPenjualan.getDiskon() / 100 * menuPenjualan.getHarga())
                                                                * menuPenjualan.getJumlah()),
                                                "komentar",
                                                Optional.ofNullable(menuPenjualan.getKomentar()).orElse("")))
                                        .toList()),
                                Map.entry("totalBayar", penjualan.getTotalBayar()),
                                Map.entry("totalHarga", penjualan.getTotalHarga()),
                                Map.entry("diskon", penjualan.getDiskon()),
                                Map.entry("namaPelanggan", penjualan.getNamaPelanggan()),
                                Map.entry("kembalian", penjualan.getTotalBayar() - penjualan.getTotalHarga()),
                                Map.entry("tipePembayaran",
                                        Optional.ofNullable(penjualan.getTipePembayaran()).map(TipePembayaran::toString)
                                                .orElse("")),
                                Map.entry("createdAt", penjualan.getCreatedAt()),
                                Map.entry("editedAt", penjualan.getEditedAt()));
                    } else {
                        httpCode = HTTPCode.NOT_FOUND;
                        data = new ErrorMessage(httpCode, "Item Cart tidak ditemukan");
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

}
