package com.kritz.restfulapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kritz.restfulapi.model.Bahan;

public interface BahanRepository extends JpaRepository<Bahan, String> {
}
