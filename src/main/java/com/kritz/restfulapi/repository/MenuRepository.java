package com.kritz.restfulapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kritz.restfulapi.model.Menu;

public interface MenuRepository extends JpaRepository<Menu, String> {
}
