package com.moodbites.restfulapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.moodbites.restfulapi.model.Menu;

public interface MenuRepository extends JpaRepository<Menu, String> {
}
