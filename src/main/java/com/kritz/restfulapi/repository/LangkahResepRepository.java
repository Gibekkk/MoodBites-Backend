package com.kritz.restfulapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kritz.restfulapi.model.LangkahResep;

public interface LangkahResepRepository extends JpaRepository<LangkahResep, String> {
}
