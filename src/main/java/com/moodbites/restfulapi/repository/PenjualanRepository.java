package com.moodbites.restfulapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.moodbites.restfulapi.model.Penjualan;
import com.moodbites.restfulapi.model.Toko;
import com.moodbites.restfulapi.model.enums.StatusPenjualan;

public interface PenjualanRepository extends JpaRepository<Penjualan, String> {
    public Optional<Penjualan> findByIdTokoAndDeletedAtIsNullAndStatusPenjualan(Toko toko, StatusPenjualan statusPenjualan);
    public Optional<Penjualan> findByIdTokoAndDeletedAtIsNullAndStatusPenjualanNot(Toko toko, StatusPenjualan statusPenjualan);
}
