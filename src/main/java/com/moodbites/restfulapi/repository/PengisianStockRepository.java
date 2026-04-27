package com.moodbites.restfulapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.moodbites.restfulapi.model.PengisianStock;

public interface PengisianStockRepository extends JpaRepository<PengisianStock, String> {
}
