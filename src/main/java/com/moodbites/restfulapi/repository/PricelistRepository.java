package com.moodbites.restfulapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.moodbites.restfulapi.model.Pricelist;

public interface PricelistRepository extends JpaRepository<Pricelist, String> {
}
