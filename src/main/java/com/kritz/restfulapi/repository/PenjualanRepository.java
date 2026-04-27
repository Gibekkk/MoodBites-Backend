package com.kritz.restfulapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kritz.restfulapi.model.Penjualan;
import com.kritz.restfulapi.model.Toko;
import com.kritz.restfulapi.model.enums.StatusPenjualan;

public interface PenjualanRepository extends JpaRepository<Penjualan, String> {
    public Optional<Penjualan> findByIdTokoAndDeletedAtIsNullAndStatusPenjualan(Toko toko, StatusPenjualan statusPenjualan);
    public Optional<Penjualan> findByIdTokoAndDeletedAtIsNullAndStatusPenjualanNot(Toko toko, StatusPenjualan statusPenjualan);
}
