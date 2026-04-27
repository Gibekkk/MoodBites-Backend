package com.moodbites.restfulapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.moodbites.restfulapi.model.Notifikasi;

public interface NotifikasiRepository extends JpaRepository<Notifikasi, String> {
}
