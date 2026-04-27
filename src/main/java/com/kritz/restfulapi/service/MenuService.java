package com.kritz.restfulapi.service;

import java.util.HashSet;
import java.util.Optional;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.kritz.restfulapi.dto.EditPesananDTO;
import com.kritz.restfulapi.dto.LangkahDTO;
import com.kritz.restfulapi.dto.MenuDTO;
import com.kritz.restfulapi.dto.PaymentDTO;
import com.kritz.restfulapi.dto.PesananDTO;
import com.kritz.restfulapi.dto.lists.BahanList;
import com.kritz.restfulapi.model.Bahan;
import com.kritz.restfulapi.model.BahanResep;
import com.kritz.restfulapi.model.LangkahResep;
import com.kritz.restfulapi.model.Menu;
import com.kritz.restfulapi.model.MenuPenjualan;
import com.kritz.restfulapi.model.Penjualan;
import com.kritz.restfulapi.model.enums.StatusPenjualan;
import com.kritz.restfulapi.model.enums.TipePembayaran;
import com.kritz.restfulapi.model.Pricelist;
import com.kritz.restfulapi.model.Toko;
import com.kritz.restfulapi.model.enums.Kategori;
import com.kritz.restfulapi.repository.BahanRepository;
import com.kritz.restfulapi.repository.BahanResepRepository;
import com.kritz.restfulapi.repository.LangkahResepRepository;
import com.kritz.restfulapi.repository.MenuRepository;
import com.kritz.restfulapi.repository.PricelistRepository;
import com.kritz.restfulapi.repository.PenjualanRepository;
import com.kritz.restfulapi.repository.MenuPenjualanRepository;

import jakarta.transaction.Transactional;

@Service
public class MenuService {

    @Autowired
    private BahanRepository bahanRepository;

    @Autowired
    private BahanResepRepository bahanResepRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private PenjualanRepository penjualanRepository;

    @Autowired
    private PricelistRepository pricelistRepository;

    @Autowired
    private LangkahResepRepository langkahResepRepository;

    @Autowired
    private MenuPenjualanRepository menuPenjualanRepository;

    @Autowired
    private ImageService imageService;

    @Value("${storage.upload-dir}/menu/")
    private String pathToFoto;

    @Transactional
    public void deleteMenu(Menu menu) {
        menu.getIdToko().getListMenu().remove(menu);
        menu.setIdToko(null);
        menuRepository.delete(menu);
    }

    public void flagDeleteMenu(Menu menu) {
        menu.setDeletedAt(LocalDateTime.now());
        menuRepository.save(menu);
    }

    public String getImage(Menu menu) {
        return imageService.getImage(menu.getImageUrl());
    }

    public int getMenuStock(Menu menu) {
        int stock = 100;
        for (BahanResep bahanResep : menu.getListBahanResep()) {
            Bahan bahan = bahanResep.getIdBahan();
            int bahanStock = (int) Math.floor((double) bahan.getIdStock().getStock() / bahanResep.getJumlah());
            if (bahanStock < stock) {
                stock = bahanStock;
            }
        }
        return stock;
    }

    public Optional<Penjualan> findCurrentCartNotPurchased(Toko toko) {
        return penjualanRepository.findByIdTokoAndDeletedAtIsNullAndStatusPenjualanNot(toko, StatusPenjualan.TERJUAL);
    }

    public Optional<Penjualan> findCurrentCart(Toko toko) {
        return penjualanRepository.findByIdTokoAndDeletedAtIsNullAndStatusPenjualan(toko, StatusPenjualan.KERANJANG);
    }

    public Optional<Penjualan> findCurrentCartPayment(Toko toko) {
        return penjualanRepository.findByIdTokoAndDeletedAtIsNullAndStatusPenjualan(toko, StatusPenjualan.PEMBAYARAN);
    }

    public Penjualan editPesanan(MenuPenjualan itemPenjualan, EditPesananDTO editPesananDTO) {
        for (BahanResep bahanResep : itemPenjualan.getIdMenu().getListBahanResep()) {
            Bahan bahan = bahanResep.getIdBahan();
            bahan.getIdStock()
                    .setStock(bahan.getIdStock().getStock() + (itemPenjualan.getJumlah() * bahanResep.getJumlah()));
            bahan.getIdStock()
                    .setStock(bahan.getIdStock().getStock() - (bahanResep.getJumlah() * editPesananDTO.getJumlah()));
            bahanRepository.save(bahan);
        }
        itemPenjualan.setKomentar(editPesananDTO.getDeskripsi());
        itemPenjualan.setJumlah(editPesananDTO.getJumlah());
        itemPenjualan.setHarga(itemPenjualan.getIdMenu().getIdPricelist().getHarga());
        itemPenjualan.setDiskon(itemPenjualan.getIdMenu().getIdPricelist().getDiskon());
        menuPenjualanRepository.save(itemPenjualan);
        Penjualan penjualan = itemPenjualan.getIdPenjualan();
        for (MenuPenjualan mp : penjualan.getListMenuPenjualan()) {
            if (mp.getId().equals(itemPenjualan.getId())) {
                mp.setJumlah(editPesananDTO.getJumlah());
                mp.setKomentar(editPesananDTO.getDeskripsi());
                break;
            }
        }
        return penjualan;
    }

    @Transactional
    public Penjualan deletePesanan(MenuPenjualan itemPenjualan) {
        for (BahanResep bahanResep : itemPenjualan.getIdMenu().getListBahanResep()) {
            Bahan bahan = bahanResep.getIdBahan();
            bahan.getIdStock()
                    .setStock(bahan.getIdStock().getStock() + (itemPenjualan.getJumlah() * bahanResep.getJumlah()));
            bahanRepository.save(bahan);
        }
        Penjualan penjualan = itemPenjualan.getIdPenjualan();
        penjualan.getListMenuPenjualan().remove(itemPenjualan);
        menuPenjualanRepository.delete(itemPenjualan);
        return penjualan;
    }

    public Penjualan cartToPayment(Penjualan penjualan) {
        penjualan.setStatusPenjualan(StatusPenjualan.PEMBAYARAN);
        penjualan.setEditedAt(LocalDateTime.now());
        return penjualanRepository.save(penjualan);
    }

    public Penjualan cartBackToCart(Penjualan penjualan) {
        penjualan.setStatusPenjualan(StatusPenjualan.KERANJANG);
        penjualan.setEditedAt(LocalDateTime.now());
        return penjualanRepository.save(penjualan);
    }

    public Penjualan completePayment(Penjualan penjualan, PaymentDTO paymentDTO) {
        penjualan.setEditedAt(LocalDateTime.now());
        penjualan.setNamaPelanggan(paymentDTO.getNamaPelanggan());
        penjualan.setTipePembayaran(TipePembayaran.fromString(paymentDTO.getTipePembayaran()));
        penjualan.setTotalBayar(paymentDTO.getTotalBayar());
        penjualan.setStatusPenjualan(StatusPenjualan.TERJUAL);
        return penjualanRepository.save(penjualan);
    }

    public Menu addMenu(Toko toko, MenuDTO menuDTO) {
        Menu menu = new Menu();
        menu.setIdToko(toko);
        menu.setNama(menuDTO.getNama());
        menu.setDeskripsi(menuDTO.getDeskripsi());
        menu.setKategori(Kategori.fromString(menuDTO.getKategori()));
        menu.setCreatedAt(LocalDateTime.now());
        menu.setEditedAt(LocalDateTime.now());
        menu.setImageUrl(imageService.saveImage(menuDTO.getImage(), pathToFoto));
        menu = menuRepository.save(menu);

        Pricelist pricelist = new Pricelist();
        pricelist.setIdMenu(menu);
        pricelist.setHarga(menuDTO.getHarga());
        pricelist = pricelistRepository.save(pricelist);

        for (BahanList bahanList : menuDTO.getListBahan()) {
            Optional<Bahan> bahanOpt = bahanRepository.findById(bahanList.getIdBahan());
            if (bahanOpt.isPresent() && bahanOpt.get().getIdToko().getId().equals(toko.getId())
                    && bahanOpt.get().getDeletedAt() == null) {
                BahanResep bahanResep = new BahanResep();
                bahanResep.setIdMenu(menu);
                bahanResep.setIdBahan(bahanOpt.get());
                bahanResep.setJumlah(bahanList.getJumlahBahan());
                bahanResepRepository.save(bahanResep);
            } else {
                throw new IllegalArgumentException("Bahan dengan ID " + bahanList.getIdBahan() + " tidak ditemukan.");
            }
        }

        for (LangkahDTO langkahDTO : menuDTO.getListLangkah()) {
            LangkahResep langkahResep = new LangkahResep();
            langkahResep.setIdMenu(menu);
            langkahResep.setDeskripsi(langkahDTO.getDeskripsi());
            langkahResep.setUrutan(langkahDTO.getUrutan());
            langkahResepRepository.save(langkahResep);
        }
        return menu;
    }

    @Transactional
    public Menu editMenu(MenuDTO menuDTO, Menu menu, Toko toko) {
        imageService.deleteImage(menu.getImageUrl());

        menu.setNama(menuDTO.getNama());
        menu.setDeskripsi(menuDTO.getDeskripsi());
        menu.setKategori(Kategori.fromString(menuDTO.getKategori()));
        menu.setEditedAt(LocalDateTime.now());
        menu.setImageUrl(imageService.saveImage(menuDTO.getImage(), pathToFoto));
        menu = menuRepository.save(menu);

        Pricelist pricelist = menu.getIdPricelist();
        pricelist.setIdMenu(menu);
        pricelist.setHarga(menuDTO.getHarga());
        pricelist = pricelistRepository.save(pricelist);

        bahanResepRepository.deleteAll(menu.getListBahanResep());
        menu.getListBahanResep().clear();

        for (BahanList bahanList : menuDTO.getListBahan()) {
            Optional<Bahan> bahanOpt = bahanRepository.findById(bahanList.getIdBahan());
            if (bahanOpt.isPresent() && bahanOpt.get().getIdToko().getId().equals(toko.getId())
                    && bahanOpt.get().getDeletedAt() == null) {
                BahanResep bahanResep = new BahanResep();
                bahanResep.setIdMenu(menu);
                bahanResep.setIdBahan(bahanOpt.get());
                bahanResep.setJumlah(bahanList.getJumlahBahan());
                menu.getListBahanResep().add(bahanResep);
                bahanResepRepository.save(bahanResep);
            } else {
                throw new IllegalArgumentException("Bahan dengan ID " + bahanList.getIdBahan() + " tidak ditemukan.");
            }
        }

        langkahResepRepository.deleteAll(menu.getListLangkahResep());
        menu.getListLangkahResep().clear();

        for (LangkahDTO langkahDTO : menuDTO.getListLangkah()) {
            LangkahResep langkahResep = new LangkahResep();
            langkahResep.setIdMenu(menu);
            langkahResep.setDeskripsi(langkahDTO.getDeskripsi());
            langkahResep.setUrutan(langkahDTO.getUrutan());
            menu.getListLangkahResep().add(langkahResep);
            langkahResepRepository.save(langkahResep);
        }
        return menu;
    }

    public Penjualan addPesanan(Toko toko, PesananDTO pesananDTO) {
        Penjualan penjualan = penjualanRepository
                .findByIdTokoAndDeletedAtIsNullAndStatusPenjualan(toko, StatusPenjualan.KERANJANG)
                .orElseGet(() -> {
                    Penjualan newPenjualan = new Penjualan();
                    newPenjualan.setIdToko(toko);
                    newPenjualan.setStatusPenjualan(StatusPenjualan.KERANJANG);
                    newPenjualan.setTotalBayar(0);
                    newPenjualan.setDiskon(0);
                    newPenjualan.setCreatedAt(LocalDateTime.now());
                    newPenjualan.setEditedAt(LocalDateTime.now());
                    newPenjualan.setListMenuPenjualan(new HashSet<>());
                    return penjualanRepository.save(newPenjualan);
                });

        MenuPenjualan menuPenjualan = new MenuPenjualan();
        Optional<Menu> menuOpt = findByMenuAndToko(pesananDTO.getIdMenu(), toko);
        if (menuOpt.isPresent()) {
            menuPenjualan.setIdMenu(menuOpt.get());
            menuPenjualan.setIdPenjualan(penjualan);
            menuPenjualan.setKomentar(pesananDTO.getDeskripsi());
            menuPenjualan.setJumlah(pesananDTO.getJumlah());
            menuPenjualan.setHarga(menuOpt.get().getIdPricelist().getHarga());
            menuPenjualan.setDiskon(menuOpt.get().getIdPricelist().getDiskon());
            penjualan.getListMenuPenjualan().add(menuPenjualan);
            menuPenjualanRepository.save(menuPenjualan);

            for (BahanResep bahanResep : menuOpt.get().getListBahanResep()) {
                Bahan bahan = bahanResep.getIdBahan();
                bahan.getIdStock()
                        .setStock(bahan.getIdStock().getStock() - (bahanResep.getJumlah() * pesananDTO.getJumlah()));
                bahanRepository.save(bahan);
            }
        } else {
            throw new IllegalArgumentException("Menu dengan ID " + pesananDTO.getIdMenu() + " tidak ditemukan.");
        }
        return penjualan;
    }

    public Optional<Menu> findByMenuAndToko(String idMenu, Toko toko) {
        Optional<Menu> menuOpt = menuRepository.findById(idMenu);
        if (menuOpt.isPresent() && menuOpt.get().getIdToko().getId().equals(toko.getId())
                && menuOpt.get().getDeletedAt() == null) {
            return menuOpt;
        } else {
            return Optional.empty();
        }
    }
}
