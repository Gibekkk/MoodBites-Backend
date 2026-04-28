package com.moodbites.restfulapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.moodbites.restfulapi.model.Flavors;

public interface FlavorsRepository extends JpaRepository<Flavors, String> {
}
