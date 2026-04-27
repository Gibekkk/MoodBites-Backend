package com.kritz.restfulapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kritz.restfulapi.model.Stock;

public interface StockRepository extends JpaRepository<Stock, String> {
}
