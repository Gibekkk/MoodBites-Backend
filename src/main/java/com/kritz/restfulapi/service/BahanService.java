package com.kritz.restfulapi.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.kritz.restfulapi.dto.BahanDTO;
import com.kritz.restfulapi.dto.RestockDTO;
import com.kritz.restfulapi.dto.lists.BahanList;
import com.kritz.restfulapi.model.Bahan;
import com.kritz.restfulapi.model.BahanRestock;
import com.kritz.restfulapi.model.PengisianStock;
import com.kritz.restfulapi.model.Stock;
import com.kritz.restfulapi.model.Toko;
import com.kritz.restfulapi.model.enums.SatuanBahan;
import com.kritz.restfulapi.repository.BahanRepository;
import com.kritz.restfulapi.repository.StockRepository;
import com.kritz.restfulapi.repository.BahanRestockRepository;
import com.kritz.restfulapi.repository.PengisianStockRepository;
import jakarta.transaction.Transactional;

@Service
public class BahanService {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private BahanRepository bahanRepository;

    @Autowired
    private BahanRestockRepository bahanRestockRepository;

    @Autowired
    private PengisianStockRepository pengisianStockRepository;

    @Autowired
    private ImageService imageService;

    @Value("${storage.upload-dir}/bahan/")
    private String pathToFoto;

    BahanService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Transactional
    public void deleteBahan(Bahan bahan) {
        bahan.getIdToko().getListBahan().remove(bahan);
        bahan.setIdToko(null);
        bahanRepository.delete(bahan);
    }

    public String getImage(Bahan bahan) {
        return imageService.getImage(bahan.getImageUrl());
    }

    public void flagDeleteBahan(Bahan bahan) {
        bahan.setDeletedAt(LocalDateTime.now());
        bahanRepository.save(bahan);
    }

    public Bahan addBahan(BahanDTO bahanDTO, Toko toko) {
        Bahan bahan = new Bahan();
        bahan.setCreatedAt(LocalDateTime.now());
        bahan.setEditedAt(LocalDateTime.now());
        bahan.setNama(bahanDTO.getNamaBahan());
        bahan.setDeskripsi(bahanDTO.getDeskripsi());
        bahan.setSatuanBahan(SatuanBahan.fromString(bahanDTO.getSatuanBahan()));
        bahan.setImageUrl(imageService.saveImage(bahanDTO.getImage(), pathToFoto));
        bahan.setIdToko(toko);
        bahan = bahanRepository.save(bahan);

        Stock stock = new Stock();
        stock.setIdBahan(bahan);
        stock.setStock(bahanDTO.getJumlahBahan());
        bahan.setIdStock(stock);
        stockRepository.save(stock);
        return bahan;
    }

    public Bahan editBahan(BahanDTO bahanDTO, Bahan bahan) {
        imageService.deleteImage(bahan.getImageUrl());

        bahan.setEditedAt(LocalDateTime.now());
        bahan.setNama(bahanDTO.getNamaBahan());
        bahan.setDeskripsi(bahanDTO.getDeskripsi());
        bahan.setSatuanBahan(SatuanBahan.fromString(bahanDTO.getSatuanBahan()));
        bahan.setImageUrl(imageService.saveImage(bahanDTO.getImage(), pathToFoto));
        return bahanRepository.save(bahan);
    }

    public ArrayList<Map<String, Object>> restockBahan(Toko toko, RestockDTO restockDTO) {
        PengisianStock pengisianStock = new PengisianStock();
        pengisianStock.setIdToko(toko);
        pengisianStock.setCreatedAt(LocalDateTime.now());
        pengisianStock.setEditedAt(LocalDateTime.now());
        pengisianStock = pengisianStockRepository.save(pengisianStock);

        ArrayList<Map<String, Object>> restockResults = new ArrayList<>();
        for (BahanList item : restockDTO.getListBahan()) {
            Optional<Bahan> bahanOpt = bahanRepository.findById(item.getIdBahan());
            if (bahanOpt.isPresent() && bahanOpt.get().getIdToko().getId().equals(toko.getId())) {
                Bahan bahan = bahanOpt.get();
                bahan.getIdStock().setStock(bahan.getIdStock().getStock() + item.getJumlahBahan());
                bahanRepository.save(bahan);

                BahanRestock bahanRestock = new BahanRestock();
                bahanRestock.setIdPengisianStock(pengisianStock);
                bahanRestock.setIdBahan(bahan);
                bahanRestock.setJumlah(item.getJumlahBahan());
                bahanRestock = bahanRestockRepository.save(bahanRestock);

                Map<String, Object> result = Map.of(
                        "idBahan", bahan.getId(),
                        "namaBahan", bahan.getNama(),
                        "jumlahRestock", item.getJumlahBahan(),
                        "stockSekarang", bahan.getIdStock().getStock()
                );
                restockResults.add(result);
            } else {
                throw new IllegalArgumentException("Bahan dengan ID " + item.getIdBahan() + " tidak ditemukan.");
            }
        }
        return restockResults;
    }

}
