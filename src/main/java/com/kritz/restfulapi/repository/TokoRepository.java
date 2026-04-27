package com.kritz.restfulapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kritz.restfulapi.model.Toko;

public interface TokoRepository extends JpaRepository<Toko, String> {
}
