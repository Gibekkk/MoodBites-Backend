package com.moodbites.restfulapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.moodbites.restfulapi.model.Bahan;

public interface BahanRepository extends JpaRepository<Bahan, String> {
}
