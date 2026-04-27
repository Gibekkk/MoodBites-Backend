package com.kritz.restfulapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kritz.restfulapi.model.Pricelist;

public interface PricelistRepository extends JpaRepository<Pricelist, String> {
}
