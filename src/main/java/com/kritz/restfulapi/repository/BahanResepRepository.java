package com.kritz.restfulapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kritz.restfulapi.model.BahanResep;


public interface BahanResepRepository extends JpaRepository<BahanResep, String> {
}
