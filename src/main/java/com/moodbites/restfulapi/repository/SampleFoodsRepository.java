package com.moodbites.restfulapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.moodbites.restfulapi.model.SampleFoods;

public interface SampleFoodsRepository extends JpaRepository<SampleFoods, String> {
}
