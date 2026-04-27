package com.moodbites.restfulapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.moodbites.restfulapi.model.Stock;

public interface StockRepository extends JpaRepository<Stock, String> {
}
