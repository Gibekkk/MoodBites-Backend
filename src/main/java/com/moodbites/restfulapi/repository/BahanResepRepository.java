package com.moodbites.restfulapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.moodbites.restfulapi.model.BahanResep;


public interface BahanResepRepository extends JpaRepository<BahanResep, String> {
}
