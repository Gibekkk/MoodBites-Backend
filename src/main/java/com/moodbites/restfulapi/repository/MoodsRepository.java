package com.moodbites.restfulapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.moodbites.restfulapi.model.Moods;

public interface MoodsRepository extends JpaRepository<Moods, String> {
}
