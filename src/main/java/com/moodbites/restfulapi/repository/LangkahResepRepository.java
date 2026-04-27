package com.moodbites.restfulapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.moodbites.restfulapi.model.LangkahResep;

public interface LangkahResepRepository extends JpaRepository<LangkahResep, String> {
}
