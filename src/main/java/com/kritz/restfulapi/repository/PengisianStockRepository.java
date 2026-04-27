package com.kritz.restfulapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kritz.restfulapi.model.PengisianStock;

public interface PengisianStockRepository extends JpaRepository<PengisianStock, String> {
}
