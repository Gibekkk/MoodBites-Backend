package com.moodbites.restfulapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.moodbites.restfulapi.model.Toko;

public interface TokoRepository extends JpaRepository<Toko, String> {
}
