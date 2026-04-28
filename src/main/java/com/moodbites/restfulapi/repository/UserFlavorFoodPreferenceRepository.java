package com.moodbites.restfulapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.moodbites.restfulapi.model.UserFlavorFoodPreference;

import java.util.Optional;

public interface UserFlavorFoodPreferenceRepository extends JpaRepository<UserFlavorFoodPreference, String> {
}
